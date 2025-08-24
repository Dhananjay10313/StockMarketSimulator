package com.wallstreet.stock.market.simulation.repository;

import com.wallstreet.stock.market.simulation.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, UUID> {
    Optional<Holding> findByUserIdAndSymbol(UUID userId, String symbol);
}
