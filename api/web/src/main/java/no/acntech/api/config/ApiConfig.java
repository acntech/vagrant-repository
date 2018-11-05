package no.acntech.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.acntech.service.config.ServiceConfig;

@Import({
        ServiceConfig.class
})
@Configuration
public class ApiConfig {

}
