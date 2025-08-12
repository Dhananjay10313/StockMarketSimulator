package com.wallstreet.stock.market.simulation.model;
import com.wallstreet.stock.market.simulation.model.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "admin_flags")
public class AdminFlag {
    @Id
    private String key;

    @Column(columnDefinition = "jsonb")
    private String value;

    private OffsetDateTime updatedAt;

    public AdminFlag() { this.updatedAt = OffsetDateTime.now(); }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
