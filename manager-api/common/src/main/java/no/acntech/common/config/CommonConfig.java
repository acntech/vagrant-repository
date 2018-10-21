package no.acntech.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
        ApplicationProperties.class
})
@ComponentScan(basePackages = {"no.acntech.common"})
@Configuration
public class CommonConfig {

}
