package org.example.orderservice.controller;

import org.example.orderservice.model.OrderItem;
import org.example.orderservice.repository.OrderItemRepository;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order_items")
public class OrderItemController {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderItemController(OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * 🟢 Lấy danh sách tất cả mặt hàng trong đơn hàng
     */
    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllItems() {
        return ResponseEntity.ok(orderItemRepository.findAll());
    }

    /**
     * 🟢 Lấy thông tin chi tiết một mặt hàng
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getItemById(@PathVariable Long id) {
        return orderItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 🟡 Tạo mặt hàng mới (gắn vào một đơn hàng đã có)
     */
    @PostMapping
    @Transactional
    public ResponseEntity<?> createItem(@RequestBody OrderItem item) {
        if (item.getOrder() == null || item.getOrder().getId() == null) {
            return ResponseEntity.badRequest().body("❌ Cần chỉ định ID của đơn hàng!");
        }

        Optional<ResponseEntity<?>> result = orderRepository.findById(item.getOrder().getId())
                .map(order -> {
                    item.setOrder(order);
                    item.recalcTotalPrice();
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    item.setCreatedAt(now);
                    item.setUpdatedAt(now);

                    orderItemRepository.save(item);

                    // Cập nhật tổng tiền của đơn hàng
                    order.recalcTotalAmount();
                    orderRepository.save(order);

                    return ResponseEntity.ok(item);  // ResponseEntity<OrderItem> vẫn được coi là ResponseEntity<?>
                });

        return result.orElseGet(() ->
                ResponseEntity.badRequest()
                        .body("❌ Không tìm thấy Order có ID = " + item.getOrder().getId())
        );
    }


    /**
     * 🟠 Cập nhật mặt hàng
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody OrderItem itemDetails) {
        return orderItemRepository.findById(id).map(item -> {
            item.setProductName(itemDetails.getProductName());
            item.setQuantity(itemDetails.getQuantity());
            item.setUnitPrice(itemDetails.getUnitPrice());
            item.recalcTotalPrice();
            item.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            orderItemRepository.save(item);

            // Cập nhật tổng tiền của đơn hàng
            if (item.getOrder() != null) {
                item.getOrder().recalcTotalAmount();
                orderRepository.save(item.getOrder());
            }

            return ResponseEntity.ok(item);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * 🔴 Xóa một mặt hàng
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        return orderItemRepository.findById(id).map(item -> {
            var order = item.getOrder();

            // Xóa item trước
            orderItemRepository.delete(item);
            orderItemRepository.flush(); // đảm bảo Hibernate thực thi ngay xóa

            // Cập nhật tổng tiền của order
            if (order != null) {
                order.recalcTotalAmount();
                orderRepository.save(order);
            }

            return ResponseEntity.ok("✅ Xóa mặt hàng thành công!");
        }).orElse(ResponseEntity.notFound().build());
    }
}
