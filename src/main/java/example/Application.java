package example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import example.config.Properties;

/**
 * @author optim-y-takahashi
 */
@SpringBootApplication(scanBasePackages = "config")
@EnableConfigurationProperties(Properties.class)
public class Application {
	
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}
