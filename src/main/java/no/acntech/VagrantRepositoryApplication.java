package no.acntech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import no.acntech.properties.ApplicationProperties;

@EnableConfigurationProperties({
        ApplicationProperties.class
})
@EnableTransactionManagement
@SpringBootApplication
public class VagrantRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(VagrantRepositoryApplication.class, args);
    }
}
