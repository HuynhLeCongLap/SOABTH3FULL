package org.example.helloworld.controller;

import org.example.helloworld.dto.LoginRequest;
import org.example.helloworld.entity.User;
import org.example.helloworld.repository.UserRepository;
import org.example.helloworld.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie; // ✅ import thêm
import jakarta.servlet.http.HttpServletResponse;            // ✅ import thêm

@RestController
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
    User user = userRepository.findByUserName(request.getUsername().trim());

    if (user == null || !user.getPassword().equals(request.getPassword().trim())) {
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    // Tạo token
    String token = jwtUtil.generateToken(user.getUserName());

    // Lưu token vào cookie HTTP-only
    Cookie cookie = new Cookie("jwt_token", token);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(3600); // 1 giờ
    response.addCookie(cookie);

    // Trả token trong body luôn để test Postman
    return ResponseEntity.ok("{\"message\":\"Login successful\",\"token\":\"" + token + "\"}");
}


    @PostMapping("/auth")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid Authorization header");
        }
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        if (jwtUtil.validateToken(token, username)) {
            return ResponseEntity.ok("Token valid");
        } else {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }
    @PostMapping("/logout")
public ResponseEntity<?> logout(HttpServletResponse response) {
    // Xóa cookie JWT
    Cookie cookie = new Cookie("jwt_token", null);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0); // xóa cookie
    response.addCookie(cookie);

    return ResponseEntity.ok("{\"message\": \"Logout successful\"}");
}


}
