package com.wallstreet.stock.market.simulation.repository;

import com.wallstreet.stock.market.simulation.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUser_IdAndRevokedFalse(UUID userId);

    List<RefreshToken> findByExpiresAtBefore(OffsetDateTime cutoff);

    // convenience for cleanup
    void deleteByExpiresAtBefore(OffsetDateTime cutoff);
}
