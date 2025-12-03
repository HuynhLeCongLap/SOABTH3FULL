package org.example.productservice.controller;

import org.example.productservice.model.Product;
import org.example.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // --- Trang web Thymeleaf ---
    @GetMapping
    public String productsPage(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "products"; // products.html
    }

    // --- REST API ---
    @GetMapping("/api")
    @ResponseBody
    public List<Product> getAllProductsJson() {
        return productService.getAllProducts();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProductByIdJson(@PathVariable int id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api")
    @ResponseBody
    public Product createProductJson(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Product> updateProductJson(@PathVariable int id, @RequestBody Product updatedProduct) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, updatedProduct));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteProductJson(@PathVariable int id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
