package no.acntech.service.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import no.acntech.common.converter.ZonedDateTimeAttributeConverter;

@EnableJpaRepositories(basePackages = {"no.acntech.service.repository"})
@EntityScan(
        basePackageClasses = {
                Jsr310JpaConverters.class,
                ZonedDateTimeAttributeConverter.class},
        basePackages = {"no.acntech.common.model"})
@Configuration
public class DatabaseConfig {

}
