package com.sprintflow.config;

import com.sprintflow.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/health/**").permitAll()

                // Sprints — all 3 roles can read
                .requestMatchers(HttpMethod.GET,    "/api/sprints/**").hasAnyRole("MANAGER", "HR", "TRAINER")
                // HR creates/deletes sprints
                .requestMatchers(HttpMethod.POST,   "/api/sprints").hasRole("HR")
                .requestMatchers(HttpMethod.DELETE, "/api/sprints/**").hasRole("HR")
                // HR + TRAINER can update/change status
                .requestMatchers(HttpMethod.PUT,    "/api/sprints/**").hasAnyRole("HR", "TRAINER")
                .requestMatchers(HttpMethod.PATCH,  "/api/sprints/**").hasAnyRole("HR", "TRAINER")

                // Attendance — TRAINER submits, all can read
                .requestMatchers(HttpMethod.POST, "/api/attendance/**").hasRole("TRAINER")
                .requestMatchers(HttpMethod.GET,  "/api/attendance/**").hasAnyRole("MANAGER", "HR", "TRAINER")

                // Employees — MANAGER + HR manage, all can read
                .requestMatchers(HttpMethod.GET,    "/api/employees/**").hasAnyRole("MANAGER", "HR", "TRAINER")
                .requestMatchers(HttpMethod.POST,   "/api/employees/**").hasAnyRole("MANAGER", "HR")
                .requestMatchers(HttpMethod.PUT,    "/api/employees/**").hasAnyRole("MANAGER", "HR")
                .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("MANAGER")

                // Users (trainer/HR management) — MANAGER only
                .requestMatchers("/api/users/**").hasRole("MANAGER")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
