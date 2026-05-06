package org.example.thuvien.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Thuật toán băm mật khẩu chuẩn hiện nay
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để tiện test API (trong thực tế cần cân nhắc)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/books/**").hasRole("ADMIN") // Chỉ Admin mới được quản lý sách
                        .requestMatchers("/api/users/**").permitAll()    // Cho phép mọi người đăng ký user
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Mở cửa Swagger
                        .anyRequest().authenticated() // Các yêu cầu khác phải đăng nhập
                )
                .httpBasic(withDefaults()); // Sử dụng xác thực cơ bản (Username/Password)

        return http.build();
    }
}