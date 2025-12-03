package org.example.orderservice.service;

import org.example.orderservice.model.Order;
import org.example.orderservice.model.OrderItem;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final HttpServletRequest request;

    @Autowired
    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate, HttpServletRequest request) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.request = request;
    }

    /** Lấy danh sách tất cả đơn hàng */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /** Tìm đơn hàng theo ID */
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /** Tạo đơn hàng mới */
    @Transactional
    public Order createOrder(Order order) {
        double totalAmount = 0;
        String authHeader = request.getHeader("Authorization");

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("⚠️ Đơn hàng phải có ít nhất một sản phẩm!");
        }

        for (OrderItem item : order.getItems()) {
            try {
                String productUrl = "http://localhost:8081/products/" + item.getProductId();

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", authHeader);
                HttpEntity<Void> entity = new HttpEntity<>(headers);

                ResponseEntity<ProductResponse> response = restTemplate.exchange(
                        productUrl, HttpMethod.GET, entity, ProductResponse.class
                );

                ProductResponse product = response.getBody();
                if (product == null) {
                    throw new RuntimeException("❌ Không tìm thấy sản phẩm ID = " + item.getProductId());
                }

                if (product.getQuantity() < item.getQuantity()) {
                    throw new RuntimeException("❌ Sản phẩm '" + product.getName() + "' không đủ tồn kho!");
                }

                item.setProductName(product.getName());
                item.setUnitPrice(product.getPrice());
                item.recalcTotalPrice();
                item.setOrder(order);

                Timestamp now = new Timestamp(System.currentTimeMillis());
                item.setCreatedAt(now);
                item.setUpdatedAt(now);

                totalAmount += item.getTotalPrice();

            } catch (Exception e) {
                throw new RuntimeException("❌ Lỗi khi gọi product-service: " + e.getMessage());
            }
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        return orderRepository.save(order);
    }

    /** 🟢 Cập nhật đơn hàng (gọi lại product-service để lấy giá & tên) */
    @Transactional
    public Order updateOrder(Long id, Order orderDetails) {
        String authHeader = request.getHeader("Authorization");

        return orderRepository.findById(id).map(order -> {
            order.setCustomerName(orderDetails.getCustomerName());
            order.setCustomerEmail(orderDetails.getCustomerEmail());
            order.setStatus(orderDetails.getStatus() != null ? orderDetails.getStatus() : "PENDING");

            // Xóa danh sách item cũ, thay bằng danh sách mới
            if (orderDetails.getItems() != null && !orderDetails.getItems().isEmpty()) {
                order.getItems().clear();

                double totalAmount = 0;
                for (OrderItem item : orderDetails.getItems()) {
                    try {
                        String productUrl = "http://localhost:8081/products/" + item.getProductId();

                        HttpHeaders headers = new HttpHeaders();
                        headers.set("Authorization", authHeader);
                        HttpEntity<Void> entity = new HttpEntity<>(headers);

                        ResponseEntity<ProductResponse> response = restTemplate.exchange(
                                productUrl, HttpMethod.GET, entity, ProductResponse.class
                        );

                        ProductResponse product = response.getBody();
                        if (product == null) {
                            throw new RuntimeException("❌ Không tìm thấy sản phẩm ID = " + item.getProductId());
                        }

                        item.setProductName(product.getName());
                        item.setUnitPrice(product.getPrice());
                        item.recalcTotalPrice();
                        item.setOrder(order);

                        Timestamp now = new Timestamp(System.currentTimeMillis());
                        item.setCreatedAt(now);
                        item.setUpdatedAt(now);

                        totalAmount += item.getTotalPrice();

                        order.getItems().add(item);

                    } catch (Exception e) {
                        throw new RuntimeException("❌ Lỗi khi gọi product-service: " + e.getMessage());
                    }
                }

                order.setTotalAmount(totalAmount);
            }

            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return orderRepository.save(order);
        }).orElseThrow(() -> new RuntimeException("❌ Không tìm thấy Order có ID = " + id));
    }

    /** Xóa đơn hàng */
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("❌ Không tìm thấy Order có ID = " + id);
        }
        orderRepository.deleteById(id);
    }

    /** Lớp ánh xạ phản hồi từ product-service */
    static class ProductResponse {
        private Long id;
        private String name;
        private Double price;
        private Integer quantity;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
