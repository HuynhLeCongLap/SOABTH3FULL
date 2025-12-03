package org.example.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
