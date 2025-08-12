package com.wallstreet.stock.market.simulation.repository;

import com.wallstreet.stock.market.simulation.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.UUID;

@Repository
public interface StockCandleRepository extends JpaRepository<StockCandle, Long> {
    
}
