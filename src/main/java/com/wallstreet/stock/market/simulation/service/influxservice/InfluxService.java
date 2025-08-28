package com.wallstreet.stock.market.simulation.service.influxservice;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InfluxService {
    private final InfluxDBClient influxDBClient;
    private final String bucket;
    private final String org;
    private final Random random = new Random();

    public InfluxService(
            @Value("${influx.url}") String url,
            @Value("${influx.token}") String token,
            @Value("${influx.org}") String org,
            @Value("${influx.bucket}") String bucket) {
        this.influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
        this.bucket = bucket;
        this.org = org;
    }

    public void writeData(String measurement, double value) {
        Point point = Point.measurement(measurement)
                .addField("value", value)
                .time(System.currentTimeMillis(), WritePrecision.MS);
        try (WriteApi writeApi = influxDBClient.getWriteApi()) {
            writeApi.writePoint(point);
        }
    }

    public void insertRandomStockPrice() {
        // Random test data (replace these with your actual logic as needed)
        String[] symbols = { "AAPL", "GOOGL", "TSLA", "MSFT" };
        String symbol = symbols[random.nextInt(symbols.length)];
        double price = 50 + (random.nextDouble() * 250); // price between 50 and 300
        int volume = 1000 + random.nextInt(9000); // volume between 1000 and 9999

        Point point = Point
                .measurement("stock_price")
                .addTag("symbol", symbol)
                .addField("price", price)
                .addField("volume", volume)
                .time(Instant.now(), com.influxdb.client.domain.WritePrecision.S);

        try (WriteApi writeApi = influxDBClient.getWriteApi()) {
            writeApi.writePoint(bucket, org, point);
        }
    }

    public List<Map<String, Object>> getStockPricePerSecondWithGapFill(String symbol, int pastHours) {
        String flux = String.format(
                "from(bucket: \"%s\")\n" +
                        "|> range(start: -%dh)\n" +
                        "|> filter(fn: (r) => r._measurement == \"stock_price\")\n" +
                        "|> filter(fn: (r) => r.symbol == \"%s\")\n" +
                        "|> filter(fn: (r) => r._field == \"price\")\n" +
                        "|> aggregateWindow(every: 1s, fn: last, createEmpty: true)\n" +
                        "|> fill(usePrevious: true)",
                bucket, pastHours, symbol);

        List<Map<String, Object>> result = new ArrayList<>();

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, org);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Map<String, Object> point = new HashMap<>();
                // Format time as ISO string
                point.put("time", formatter.format(record.getTime()));
                point.put("symbol", record.getValueByKey("symbol"));
                point.put("price", record.getValue());
                result.add(point);
            }
        }
        return result;
    }

    public void insertStockPriceRecord(String symbol, double price, int volume, Instant timestamp) {
        Point point = Point.measurement("stock_price")
                .addTag("symbol", symbol)
                .addField("price", price)
                .addField("volume", volume)
                .time(timestamp, WritePrecision.S); // Using seconds precision for timestamp

        try (WriteApi writeApi = influxDBClient.getWriteApi()) {
            writeApi.writePoint(bucket, org, point);
        }
    }
}
