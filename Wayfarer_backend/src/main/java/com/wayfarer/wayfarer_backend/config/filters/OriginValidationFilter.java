package com.wayfarer.wayfarer_backend.config.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class OriginValidationFilter extends OncePerRequestFilter {

    private final List<String> allowedOrigins;

    public OriginValidationFilter(@Value("${app.cors.allowed-origins}") List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String method = request.getMethod();
        boolean isMutating = List.of("POST", "PUT", "DELETE", "PATCH").contains(method);

        if (isMutating) {
            String origin = request.getHeader("Origin");
            if (origin == null || !allowedOrigins.contains(origin)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Origine non autorisée");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
