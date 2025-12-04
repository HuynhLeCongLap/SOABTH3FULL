package org.example.productservice.controller;

import org.example.productservice.model.Product;
import org.example.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // --- Lấy tất cả sản phẩm ---
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // --- Lấy chi tiết sản phẩm theo ID ---
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable int id) {
        return productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    // --- Thêm sản phẩm ---
    @PostMapping()
    public Product addProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    // --- Cập nhật sản phẩm ---
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable int id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    // --- Xóa sản phẩm ---
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
    }
}
