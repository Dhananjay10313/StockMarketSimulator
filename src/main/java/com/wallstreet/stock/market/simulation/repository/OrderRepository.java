package com.wallstreet.stock.market.simulation.repository;

import com.wallstreet.stock.market.simulation.model.*;
import com.wallstreet.stock.market.simulation.model.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
// Change this line
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Custom query to find all actionable orders for a given symbol
    List<Order> findBySymbolAndStatusIn(String symbol, List<OrderStatus> statuses);

    // If you need to fetch all actionable orders across all symbols at once
    List<Order> findByStatusIn(List<OrderStatus> statuses);

    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :orderId")
    int updateOrderStatus(@Param("orderId") UUID orderId, @Param("status") OrderStatus status);

    @Query("SELECT o.userId FROM Order o WHERE o.id = :orderId")
    Optional<User> findUserByOrderId(@Param("orderId") UUID orderId);
}
