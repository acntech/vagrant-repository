package no.acntech.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.UUID;

import no.acntech.model.CreateUser;
import no.acntech.model.UpdateUserRole;
import no.acntech.model.UserRole;
import no.acntech.service.UserService;

@Configuration
public class InitializeConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializeConfig.class);
    private static final String DEFAULT_USERNAME = "admin";
    private final UserService userService;

    public InitializeConfig(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    @PostConstruct
    public void init() {
        if (userService.findUsers().isEmpty()) {
            LOGGER.warn("No users found in the database");
            final var password = UUID.randomUUID().toString();
            final var createUser = new CreateUser(DEFAULT_USERNAME, password);
            userService.createUser(createUser);
            final var updateUserRole = new UpdateUserRole(UserRole.ADMIN);
            userService.updateRole(createUser.username(), updateUserRole);
            LOGGER.info("Created default user: {} with password: {}", createUser.username(), createUser.password());
        }
    }
}
