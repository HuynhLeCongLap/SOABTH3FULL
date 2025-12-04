    package org.example.helloworld.config;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.servlet.config.annotation.CorsRegistry;
    import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

    @Configuration
    public class CorsConfig {

        @Bean
        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/**") // áp dụng cho tất cả endpoint
                            .allowedOrigins(
                                    "http://localhost:8081", // frontend Product
                                    "http://localhost:8082", // frontend Order
                                    "http://localhost:8083"  // frontend Reports
                            )
                            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                            .allowedHeaders("*")
                            .allowCredentials(true); // quan trọng: gửi cookie
                }
            };
        }
    }
