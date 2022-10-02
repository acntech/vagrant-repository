package no.acntech.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;

import no.acntech.converter.TokenAuthenticationConverter;
import no.acntech.handler.TokenAuthenticationSuccessHandler;
import no.acntech.repository.InMemorySecurityContextRepository;
import no.acntech.service.HttpRequestService;
import no.acntech.service.SecurityService;
import no.acntech.service.SecurityUserDetailsService;
import no.acntech.service.TokenService;
import no.acntech.service.UserService;

import static no.acntech.config.TokenSecurityConfig.tokenSecurity;

@EnableWebSecurity
public class WebSecurityConfig {

    private final String cookieName;
    private final HttpRequestService httpRequestService;
    private final SecurityService securityService;
    private final UserService userService;
    private final TokenService tokenService;

    public WebSecurityConfig(@Value("${acntech.security.cookie.name}") final String cookieName,
                             final HttpRequestService httpRequestService,
                             final SecurityService securityService,
                             final UserService userService,
                             final TokenService tokenService) {
        this.httpRequestService = httpRequestService;
        this.securityService = securityService;
        this.userService = userService;
        this.tokenService = tokenService;
        this.cookieName = cookieName;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        return http
                .csrf().disable()//.requireCsrfProtectionMatcher(AntMatchers.antMatcher("/login"))
                .cors().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .securityContext().securityContextRepository(securityContextRepository())
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/error",
                        "/register",
                        "/api/*/authenticate",
                        "/webjars/**",
                        "/resources/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").failureUrl("/login?error=failed").permitAll()
                .successHandler(authenticationSuccessHandler())
                .and()
                .logout().logoutSuccessUrl("/login?logout=success").deleteCookies(cookieName).permitAll()
                .and()
                .apply(tokenSecurity())
                .tokenAuthenticationConverter(tokenAuthenticationConverter())
                .requiresAuthentication(
                        AntMatchers.anyExceptMatcher(
                                "/error",
                                "/login",
                                "/register",
                                "/api/*/authenticate",
                                "/webjars/**",
                                "/resources/**"))
                .and()
                .userDetailsService(userDetailsService())
                .build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new TokenAuthenticationSuccessHandler(AntMatchers.andMatcher(
                AntMatchers.anyMatcher(),
                AntMatchers.negatedAntMatcher("/api/**")
        ), "/", httpRequestService, securityService, tokenService);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new SecurityUserDetailsService(userService, securityService);
    }

    @Bean
    public TokenAuthenticationConverter tokenAuthenticationConverter() {
        return new TokenAuthenticationConverter(AntMatchers.antMatcher("/api/**"), httpRequestService, securityService, tokenService);
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new InMemorySecurityContextRepository(httpRequestService, tokenService);
    }
}

