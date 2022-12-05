package com.udacity.pricing;

import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.domain.price.PriceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Creates a Spring Boot Application to run the Pricing Service.
 * TODO: Convert the application from a REST API to a microservice.
 */
@SpringBootApplication
@EnableEurekaClient
public class PricingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PricingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(PriceRepository repository) {
        return args -> {
            Long count = 1L;
            for(int i=1; i<=20; i++) {
                repository.save(new Price("USD",
                        new BigDecimal(ThreadLocalRandom.current().nextDouble(1, 5))
                                .multiply(new BigDecimal(5000d)).setScale(2, RoundingMode.HALF_UP),
                        count ));
                count++;
            }
        };
    }

}
