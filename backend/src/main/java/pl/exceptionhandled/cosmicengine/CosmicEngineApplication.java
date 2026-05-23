package pl.exceptionhandled.cosmicengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CosmicEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(CosmicEngineApplication.class, args);
	}

}
