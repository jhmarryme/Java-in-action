package org.example.bucket4j_caffeine_ratelimiter;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.caffeine.CaffeineProxyManager;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.distributed.remote.RemoteBucketState;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class RateLimiterHandlerInterceptor implements HandlerInterceptor {
    private final ProxyManager<String> proxyManager;

    public RateLimiterHandlerInterceptor() {
        Caffeine<String, RemoteBucketState> builder = Caffeine.newBuilder()
                .removalListener((key, graph, cause) -> {
                });
        proxyManager = new CaffeineProxyManager<>(builder, Duration.ofMinutes(10));
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (handler instanceof HandlerMethod) {
            String key = "custom-key";
            Bucket bucket = proxyManager.builder().build(key, this::bucketConfigurationByRateLimiter1);
            if (!bucket.tryConsume(1)) {
                response.setStatus(429);
                return false;
            }
        }
        return true;
    }

    private BucketConfiguration bucketConfigurationByRateLimiter1() {
        // 具体配置方案参考 https://bucket4j.com/8.14.0/toc.html#quick-start-examples
        // 配置方案1: Greedy填充token
        // 配置方案为每1秒10个token
        // 但 refill不会等待1秒重新生成10个token, 而是每100毫秒添加1个token
        return BucketConfiguration.builder()
                .addLimit(limit -> limit.capacity(1000).refillGreedy(10, Duration.ofSeconds(1)))
                .build();
    }

    private BucketConfiguration bucketConfigurationByRateLimiter2() {
        // 具体配置方案参考 https://bucket4j.com/8.14.0/toc.html#quick-start-examples
        // 配置方案2: Intervally填充token
        // 配置方案为每1秒10个token
        // refill会等待1秒重新生成10个token
        return BucketConfiguration.builder()
                .addLimit(limit -> limit.capacity(1000).refillIntervally(10, Duration.ofSeconds(1)))
                .build();
    }

    private BucketConfiguration bucketConfigurationByRateLimiter3() {
        // 具体配置方案参考 https://bucket4j.com/8.14.0/toc.html#quick-start-examples
        // 配置方案3: 固定周期填充token
        // 固定时间点 填充token
        Instant firstRefillTime = ZonedDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES).plusMinutes(1)
                .toInstant();
        return BucketConfiguration.builder()
                .addLimit(limit -> limit.capacity(5).refillIntervallyAligned(5, Duration.ofMinutes(1), firstRefillTime))
                .build();
    }
}
