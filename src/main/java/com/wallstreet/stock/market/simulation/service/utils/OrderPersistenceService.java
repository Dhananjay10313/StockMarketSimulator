package com.wallstreet.stock.market.simulation.service.utils;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import com.wallstreet.stock.market.simulation.model.Order;
import com.wallstreet.stock.market.simulation.model.User;
import com.wallstreet.stock.market.simulation.mapper.OrderMapper;
import com.wallstreet.stock.market.simulation.repository.OrderRepository;
import com.wallstreet.stock.market.simulation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
public class OrderPersistenceService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderPersistenceService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    /**
     * Converts, validates, and persists a new order to the database.
     * The @Transactional annotation ensures that all database operations within this method
     * either complete successfully or are rolled back entirely.
     *
     * @param orderDTO The order data from the request.
     * @param userId   The ID of the user placing the order.
     * @return The persisted Order entity, now containing its database-generated ID.
     */
    @Transactional
    public Order createAndPersistOrder(OrderDTO orderDTO, UUID userId) {
        // Step 1: Convert the DTO to an entity.
        Order order = OrderMapper.toNewEntity(orderDTO);

        // Step 2: Fetch and associate the User who owns this order.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        order.setUser(user);

        // Step 3: Save the fully formed entity to the database.
        return orderRepository.save(order);
    }
}
