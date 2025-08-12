package com.wallstreet.stock.market.simulation.model;
import com.wallstreet.stock.market.simulation.model.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "ipos")
public class Ipo {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String companyName;

    @Column(unique = true)
    private String symbol;

    private LocalDate expectedDate;

    private String ipoPriceRange;

    @Column(columnDefinition = "jsonb")
    private String details;

    private String status;
    private OffsetDateTime createdAt;

    public Ipo() { this.id = UUID.randomUUID(); this.createdAt = OffsetDateTime.now(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public LocalDate getExpectedDate() { return expectedDate; }
    public void setExpectedDate(LocalDate expectedDate) { this.expectedDate = expectedDate; }

    public String getIpoPriceRange() { return ipoPriceRange; }
    public void setIpoPriceRange(String ipoPriceRange) { this.ipoPriceRange = ipoPriceRange; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
