package org.example.orderservice.repository;

import org.example.orderservice.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /** 🔍 Lấy danh sách các mặt hàng theo ID đơn hàng */
    List<OrderItem> findByOrderId(Long orderId);

    /** 🗑️ Xóa tất cả mặt hàng theo ID đơn hàng */
    @Transactional
    void deleteByOrderId(Long orderId);
}
