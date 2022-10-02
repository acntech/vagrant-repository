package no.acntech.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.UUID;

import no.acntech.model.CreateUser;
import no.acntech.model.UserRole;
import no.acntech.service.SecurityService;
import no.acntech.service.UserService;

@Configuration
public class InitializeConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializeConfig.class);
    private final String adminUsername;
    private final SecurityService securityService;
    private final UserService userService;

    public InitializeConfig(@Value("${acntech.security.admin-user.username}") final String adminUsername,
                            final SecurityService securityService,
                            final UserService userService) {
        this.adminUsername = adminUsername;
        this.securityService = securityService;
        this.userService = userService;
    }

    @Transactional
    @PostConstruct
    public void init() {
        if (userService.findUsers().isEmpty()) {
            try {
                LOGGER.warn("No users found in the database");
                securityService.createSystemSession();
                final var password = UUID.randomUUID().toString();
                final var createUser = new CreateUser(adminUsername, password, UserRole.ADMIN);
                userService.createUser(createUser);
                LOGGER.info("\n\n" + """
                        #########################################################
                                            
                            Created default user:
                                - username: {}
                                - password: {}
                                            
                        #########################################################
                        """, createUser.username(), createUser.password());
            } finally {
                securityService.clearSystemSession();
            }
        }
    }
}
