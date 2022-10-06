package no.acntech.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class GeneralSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(@Value("${acntech.security.password.strength:16}") final Integer strength) {
        return new BCryptPasswordEncoder(strength);
    }
}

