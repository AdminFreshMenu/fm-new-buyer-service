package com.buyer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.buyer.repository.MongoDB")
public class FmBuyerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FmBuyerApplication.class, args);
	}

}
