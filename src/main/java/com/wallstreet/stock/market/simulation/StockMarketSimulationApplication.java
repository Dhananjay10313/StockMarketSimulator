package com.wallstreet.stock.market.simulation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockMarketSimulationApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockMarketSimulationApplication.class, args);
	}

}
