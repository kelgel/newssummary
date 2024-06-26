package com.example.newssummary.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int SC_TOO_MANY_REQUESTS = 429; // 429 상태 코드를 직접 정의
    private final ConcurrentHashMap<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    private final int limit = 100; // 요청 수 제한
    private final long timeFrame = 86400; // 24시간 (초 단위)

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientIp = request.getRemoteAddr();

        RateLimiter rateLimiter = limiters.computeIfAbsent(clientIp, k -> new RateLimiter(limit, timeFrame));
        if (rateLimiter.allowRequest()) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(SC_TOO_MANY_REQUESTS);
            response.getWriter().write("Too Many Requests - Rate limit exceeded");
        }
    }

    @Override
    protected void initFilterBean() throws ServletException {
        // Custom initialization logic if necessary
    }

    private static class RateLimiter {
        private final int limit;
        private final long timeFrame;
        private final ConcurrentHashMap<Long, Integer> requests = new ConcurrentHashMap<>();

        RateLimiter(int limit, long timeFrame) {
            this.limit = limit;
            this.timeFrame = timeFrame;
        }

        boolean allowRequest() {
            long now = System.currentTimeMillis() / 1000;
            requests.merge(now, 1, Integer::sum);
            requests.keySet().removeIf(time -> time < now - timeFrame);

            int requestCount = requests.values().stream().mapToInt(Integer::intValue).sum();
            return requestCount <= limit;
        }
    }
}
