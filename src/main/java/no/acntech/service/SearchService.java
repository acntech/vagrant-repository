package no.acntech.service;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.jooq.exception.NoDataFoundException;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import no.acntech.model.Box;
import no.acntech.model.SearchOrder;
import no.acntech.model.SearchProviderType;
import no.acntech.model.SearchSort;
import no.acntech.model.Version;
import no.acntech.model.VersionStatus;

import static no.acntech.model.tables.Boxes.BOXES;
import static no.acntech.model.tables.Members.MEMBERS;
import static no.acntech.model.tables.Organizations.ORGANIZATIONS;
import static no.acntech.model.tables.Users.USERS;
import static no.acntech.model.tables.Versions.VERSIONS;

@Validated
@Service
public class SearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
    private final DSLContext context;
    private final ConversionService conversionService;
    private final SecurityService securityService;

    public SearchService(final DSLContext context,
                         final ConversionService conversionService,
                         final SecurityService securityService) {
        this.context = context;
        this.conversionService = conversionService;
        this.securityService = securityService;
    }

    public List<Box> searchBoxes(final String q,
                                 final SearchProviderType provider, // TODO: Handle
                                 @NotNull final SearchSort sort,
                                 @NotNull final SearchOrder order,
                                 @NotNull @Min(1) final Integer limit,
                                 @NotNull @Min(1) final Integer page) {
        LOGGER.info("Search for boxes");
        final var fields = ArrayUtils.addAll(BOXES.fields(), ORGANIZATIONS.NAME);
        try (final var select = context.select(fields)) {
            try (final var organizationJoin = select.from(BOXES)
                    .join(ORGANIZATIONS).on(BOXES.ORGANIZATION_ID.eq(ORGANIZATIONS.ID))) {
                try (final var membersJoin = organizationJoin
                        .join(MEMBERS).on(MEMBERS.ORGANIZATION_ID.eq(ORGANIZATIONS.ID))) {
                    try (final var usersJoin = membersJoin
                            .join(USERS).on(USERS.ID.eq(MEMBERS.USER_ID))) {
                        final Integer offset = (page - 1) * limit;
                        final var result = usersJoin
                                .where(where(q))
                                .orderBy(orderBy(sort, order))
                                .limit(offset, limit)
                                .fetch();
                        return result.stream()
                                .map(record -> conversionService.convert(record, Box.class))
                                .collect(Collectors.toList());
                    }
                }
            }
        }
    }

    public Version searchCurrentVersion(@NotNull final Integer boxId) {
        try (final var select = context.selectFrom(VERSIONS)) {
            final var record = select
                    .where(VERSIONS.BOX_ID.eq(boxId).and(VERSIONS.STATUS.eq(VersionStatus.ACTIVE.name())))
                    .orderBy(VERSIONS.ID.desc())
                    .limit(1)
                    .fetchSingle();
            return conversionService.convert(record, Version.class);
        } catch (NoDataFoundException e) {
            return null;
        }
    }

    private Condition where(final String q) {
        final var username = securityService.getUsername();
        if (StringUtils.isBlank(q)) {
            return DSL.and(BOXES.PRIVATE.isFalse().or(USERS.USERNAME.eq(username)),
                    ORGANIZATIONS.PRIVATE.isFalse().or(USERS.USERNAME.eq(username)));
        } else {
            return DSL.and(BOXES.NAME.eq(q).or(ORGANIZATIONS.NAME.eq(q)),
                    BOXES.PRIVATE.isFalse().or(USERS.USERNAME.eq(username)),
                    ORGANIZATIONS.PRIVATE.isFalse().or(USERS.USERNAME.eq(username)));
        }
    }

    private SortField<?> orderBy(final SearchSort sort,
                                 final SearchOrder order) {
        switch (sort) {
            case CREATED -> {
                return SearchOrder.ASC == order ? BOXES.CREATED.asc() : BOXES.CREATED.desc();
            }
            case UPDATED -> {
                return SearchOrder.ASC == order ? BOXES.MODIFIED.asc() : BOXES.MODIFIED.desc();
            }
            default -> {
                return SearchOrder.ASC == order ? BOXES.DOWNLOADS.asc() : BOXES.DOWNLOADS.desc();
            }
        }
    }
}
