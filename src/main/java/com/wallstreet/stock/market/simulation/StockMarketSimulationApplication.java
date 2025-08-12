package com.wallstreet.stock.market.simulation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
public class StockMarketSimulationApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockMarketSimulationApplication.class, args);
	}

}
