package com.yunus.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunus.dto.ErrorResponse;
import com.yunus.service.RateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/api")) {
            String ip = getClientIP(request);
            if (!rateLimiterService.tryConsume(ip, path)) {
                log.warn("Rate limit exceeded for IP: {} on path: {}", ip, path);

                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                ErrorResponse errorResponse = new ErrorResponse(
                        "Çok fazla istek gönderildi. Lütfen daha sonra tekrar deneyin.",
                        HttpStatus.TOO_MANY_REQUESTS.value()
                );

                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
