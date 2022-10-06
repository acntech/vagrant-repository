package no.acntech.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.acntech.properties.OpenApiInfoProperties;

@EnableConfigurationProperties({OpenApiInfoProperties.class})
@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(final OpenApiInfoProperties openApiInfoProperties) {
        return new OpenAPI()
                .info(new Info()
                        .title(openApiInfoProperties.getTitle())
                        .description(openApiInfoProperties.getDescription())
                        .version(openApiInfoProperties.getVersion())
                );
    }
}
