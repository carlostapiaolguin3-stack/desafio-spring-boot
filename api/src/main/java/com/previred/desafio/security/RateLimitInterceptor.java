package com.previred.desafio.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

// limita requests por IP en una ventana de tiempo para prevenir fuerza bruta en el login
@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    @Value("${rate.limit.max-requests}")
    private int maxRequests;

    @Value("${rate.limit.window-ms}")
    private long windowMs;

    private final ConcurrentHashMap<String, Deque<Long>> requestTimestamps = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = resolveClientIp(request);
        final long now = System.currentTimeMillis();
        final int[] count = {0};

        requestTimestamps.compute(clientIp, (ip, timestamps) -> {
            if (timestamps == null) timestamps = new ArrayDeque<>();
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowMs) {
                timestamps.pollFirst();
            }
            timestamps.addLast(now);
            count[0] = timestamps.size();
            return timestamps;
        });

        if (count[0] > maxRequests) {
            log.warn("Rate limit excedido para IP: {} ({} requests en {}ms)", clientIp, count[0], windowMs);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Demasiadas solicitudes. Intente nuevamente en un momento.\"}");
            return false;
        }

        return true;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
