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

import static no.acntech.model.tables.Boxes.BOXES;
import static no.acntech.model.tables.Organizations.ORGANIZATIONS;
import static no.acntech.model.tables.Versions.VERSIONS;

@Validated
@Service
public class SearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
    private final DSLContext context;
    private final ConversionService conversionService;

    public SearchService(final DSLContext context,
                         final ConversionService conversionService) {
        this.context = context;
        this.conversionService = conversionService;
    }

    public List<Box> searchBoxes(final String q,
                                 final SearchProviderType provider, // TODO Handle
                                 @NotNull final SearchSort sort,
                                 @NotNull final SearchOrder order,
                                 @NotNull @Min(1) final Integer limit,
                                 @NotNull @Min(1) final Integer page) {
        LOGGER.info("Search for boxes");
        final var fields = ArrayUtils.addAll(BOXES.fields(), ORGANIZATIONS.NAME);
        try (final var select = context.select(fields)) {
            try (final var join = select.from(BOXES)
                    .join(ORGANIZATIONS).on(BOXES.ORGANIZATION_ID.eq(ORGANIZATIONS.ID))) {
                final Integer offset = (page - 1) * limit;
                final var result = join
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

    public Version searchCurrentVersion(@NotNull final Integer boxId) {
        try (final var select = context.selectFrom(VERSIONS)) {
            final var record = select
                    .where(VERSIONS.BOX_ID.eq(boxId))
                    .orderBy(VERSIONS.ID.desc())
                    .limit(1)
                    .fetchSingle();
            return conversionService.convert(record, Version.class);
        } catch (NoDataFoundException e) {
            return null;
        }
    }

    private Condition where(final String q) {
        if (StringUtils.isBlank(q)) {
            return DSL.trueCondition();
        } else {
            return BOXES.NAME.eq(q).or(ORGANIZATIONS.NAME.eq(q));
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
