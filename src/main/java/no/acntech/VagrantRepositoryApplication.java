package no.acntech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import no.acntech.properties.ApplicationProperties;

@EnableConfigurationProperties({
        ApplicationProperties.class
})
@SpringBootApplication
public class VagrantRepositoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(VagrantRepositoryApplication.class, args);
	}
}
