package no.acntech.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.acntech.common.config.CommonConfig;

@Import({
        CommonConfig.class
})
@ComponentScan(basePackages = {"no.acntech.service"})
@Configuration
public class ServiceConfig {

}
