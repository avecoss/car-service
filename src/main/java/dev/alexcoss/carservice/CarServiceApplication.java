package dev.alexcoss.carservice;

import dev.alexcoss.carservice.service.CsvCarService;
import dev.alexcoss.carservice.standalone.StartupRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class CarServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarServiceApplication.class, args);
	}

    @Bean
    @Profile("standalone")
    public StartupRunner startupRunner(CsvCarService csvCarService) {
        return new StartupRunner(csvCarService);
    }
}
