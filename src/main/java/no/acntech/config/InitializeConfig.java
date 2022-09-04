package no.acntech.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.UUID;

import no.acntech.model.CreateUser;
import no.acntech.model.Role;
import no.acntech.model.UpdateUserRole;
import no.acntech.service.UserService;

@Configuration
public class InitializeConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializeConfig.class);
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_NAME = "Admin";
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
            final var createUser = CreateUser.builder()
                    .username(DEFAULT_USERNAME)
                    .name(DEFAULT_NAME)
                    .password(password)
                    .build();
            userService.createUser(createUser);
            final var updateUserRole = new UpdateUserRole(DEFAULT_USERNAME, Role.ADMIN);
            userService.updateRole(updateUserRole);
            LOGGER.info("Created default user: {} with password: {}", createUser.getUsername(), createUser.getPassword());
        }
    }
}
