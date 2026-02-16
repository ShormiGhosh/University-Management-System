package com.example.student_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - login page and assets
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/api/auth/**").permitAll()
                        
                        // Allow all resources in teacher and student folders (HTML, CSS, JS loaded will check via JS)
                        .requestMatchers("/teacher/**", "/student/**").permitAll()

                        // API endpoints - Course delete only for TEACHER
                        .requestMatchers(HttpMethod.DELETE, "/api/course/**").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.POST, "/api/course/**").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/api/course/**").hasRole("TEACHER")

                        // Student endpoints - Teachers can do everything
                        .requestMatchers(HttpMethod.DELETE, "/api/student/**").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.POST, "/api/student/**").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/api/student/**").hasAnyRole("STUDENT", "TEACHER")

                        // Teacher endpoints - Only TEACHER role
                        .requestMatchers(HttpMethod.DELETE, "/api/teacher/**").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.POST, "/api/teacher/**").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/api/teacher/**").hasRole("TEACHER")

                        // Department endpoints - Only TEACHER role
                        .requestMatchers(HttpMethod.DELETE, "/api/dept/**").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.POST, "/api/dept/**").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/api/dept/**").hasRole("TEACHER")

                        // Both roles can read (GET requests)
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("STUDENT", "TEACHER")

                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Invalid credentials\"}");
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}