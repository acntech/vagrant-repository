package no.acntech.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import no.acntech.converter.BearerTokenAuthenticationConverter;
import no.acntech.filter.BearerTokenAuthenticationFilter;
import no.acntech.service.TokenService;

@EnableWebSecurity
public class WebSecurityConfig {

    private final TokenService tokenService;

    public WebSecurityConfig(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .addFilterAfter(new BearerTokenAuthenticationFilter(authenticationConverter()), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                //.antMatchers("/api/authenticate").permitAll()
                .anyRequest().authenticated()
                .and()
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/api/authenticate");
    }

    @Bean
    public BearerTokenAuthenticationConverter authenticationConverter() {
        return new BearerTokenAuthenticationConverter(tokenService);
    }

    @Bean
    public PasswordEncoder passwordEncoder(@Value("${acntech.security.password.strength:16}") Integer strength) {
        return new BCryptPasswordEncoder(strength);
    }
}

