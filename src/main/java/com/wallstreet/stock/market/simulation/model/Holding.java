package com.wallstreet.stock.market.simulation.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "holdings",
    schema = "public",
    uniqueConstraints = {
        // Corresponds to: constraint holdings_user_id_symbol_key unique (user_id, symbol)
        @UniqueConstraint(name = "holdings_user_id_symbol_key", columnNames = {"user_id", "symbol"})
    },
    indexes = {
        // Corresponds to the CREATE INDEX statements
        @Index(name = "idx_holdings_user", columnList = "user_id"),
        @Index(name = "idx_holdings_symbol", columnList = "symbol")
    }
)
@Getter
@Setter
@NoArgsConstructor // JPA requires a no-argument constructor
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Handles `default gen_random_uuid()`
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * This establishes the many-to-one relationship with the User entity.
     * The `user_id` column in the `holdings` table is mapped by this field.
     * FetchType.LAZY is a performance best practice, preventing the User object
     * from being loaded from the database until it's explicitly accessed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, 
                foreignKey = @ForeignKey(name = "holdings_user_id_fkey"))
    private User user;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "qty", nullable = false)
    private Long qty = 0L;

    @Column(name = "avg_price", nullable = false, precision = 18, scale = 6)
    private BigDecimal avgPrice = BigDecimal.ZERO;

    @Column(name = "reserved_qty", nullable = false)
    private Long reservedQty = 0L;

    /**
     * This annotation automatically sets the timestamp when the entity is updated.
     * Corresponds to `default now()` and will update on every modification.
     * Using OffsetDateTime for `timestamp with time zone`.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    // You can add custom constructors, equals(), hashCode(), and toString() methods as needed.
}
