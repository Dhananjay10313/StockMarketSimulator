package com.wallstreet.stock.market.simulation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;



import com.wallstreet.stock.market.simulation.service.influxservice.InfluxService;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final InfluxService influxService;

    public StockController(InfluxService influxService) {
        this.influxService = influxService;
    }

    @GetMapping("/{symbol}/prices")
    public List<Map<String, Object>> getStockPrices(
        @PathVariable String symbol,
        @RequestParam(defaultValue = "5") int hours) {
        // Returns list of price points per second over past 'hours'
        return influxService.getStockPricePerSecondWithGapFill(symbol, hours);
    }
}
