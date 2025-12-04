package org.example.productservice.controller;

import org.example.productservice.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductPageController {

    private final ProductService productService;

    public ProductPageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products-page")
    public String productsPage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }
}

