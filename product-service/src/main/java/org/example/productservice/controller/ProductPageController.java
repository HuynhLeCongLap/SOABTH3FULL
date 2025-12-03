package org.example.productservice.controller;

import org.example.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductPageController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products") // <-- URL HTML
    public String productsPage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products"; // products.html trong src/main/resources/templates
    }
}
