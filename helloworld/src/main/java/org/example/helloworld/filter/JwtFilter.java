package org.example.helloworld.filter;

import java.io.IOException;

import org.example.helloworld.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // ------------------------
        // 1️⃣ BỎ QUA JWT CHO URL PUBLIC
        // ------------------------
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);

        // Không có token
        if (token == null) {
            unauthorized(response, "Token missing");
            return;
        }

        // ------------------------
        // 2️⃣ VALIDATE TOKEN
        // ------------------------
        try {
            String username = jwtUtil.extractUsername(token);

            if (!jwtUtil.validateToken(token, username)) {
                unauthorized(response, "Invalid or expired token");
                return;
            }

        } catch (Exception e) {
            unauthorized(response, "Invalid or expired token");
            return;
        }

        // Token OK → tiếp tục request
        filterChain.doFilter(request, response);
    }

    // 🎯 Danh sách URL không cần JWT
    private boolean isPublicPath(String path) {

        // endpoint public
        if (path.equals("/login") ||
            path.equals("/logout") ||
            path.equals("/error") ||
            path.equals("/")) {
            return true;
        }

        // static file
        if (path.startsWith("/css") ||
            path.startsWith("/js") ||
            path.startsWith("/images") ||
            path.startsWith("/assets")) {
            return true;
        }

        // file HTML, JS, favicon
        if (path.endsWith(".html") ||
            path.endsWith(".js") ||
            path.endsWith(".css") ||
            path.endsWith(".png") ||
            path.endsWith(".jpg") ||
            path.endsWith(".ico")) {
            return true;
        }

        return false;
    }

    // 🎯 Lấy token từ Header hoặc Cookie
    private String extractToken(HttpServletRequest request) {

        // Header: Authorization: Bearer xxx
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("jwt_token".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }

        return null;
    }

    // 🎯 JSON lỗi chuẩn
    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
