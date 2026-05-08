package com.sprintflow.filter;

import com.sprintflow.config.RateLimitConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Rate Limiting Filter
 * Enforces configurable rate limits on API endpoints to prevent abuse.
 *
 * Disabled by default for local development.
 * Enable in production by setting: app.rate-limit.enabled=true
 *
 * TODO: Replace with Bucket4j for production-grade token bucket rate limiting
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimitConfig rateLimitConfig;

    @Value("${app.rate-limit.enabled:false}")
    private boolean rateLimitEnabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {

        // Skip rate limiting entirely when disabled (default for local dev)
        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String path = request.getRequestURI();
        
        // Skip rate limiting for health check and static resources
        if (path.startsWith("/actuator/health") || 
            path.startsWith("/swagger-ui") || 
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/ws/")) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean allowed;
        String key;

        // Login endpoint - strict rate limit per IP
        if (path.equals("/api/auth/login")) {
            key = getClientIP(request);
            allowed = rateLimitConfig.checkLoginLimit(key);
        }
        // Public endpoints - moderate rate limit per IP
        else if (path.startsWith("/api/auth/") || path.startsWith("/api/public/")) {
            key = getClientIP(request);
            allowed = rateLimitConfig.checkPublicLimit(key);
        }
        // Authenticated endpoints - generous rate limit per user
        else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                key = auth.getName(); // username/email
            } else {
                key = getClientIP(request);
            }
            allowed = rateLimitConfig.checkApiLimit(key);
        }
        
        if (allowed) {
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", "60");
            response.getWriter().write(
                "{\"success\":false,\"message\":\"Rate limit exceeded. Try again in 60 seconds.\",\"statusCode\":429}"
            );
        }
    }

    /**
     * Get client IP address, considering proxy headers
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
