package ru.andreymerkulov.cbrcurrency.aspect;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
public class RetryMetricsAspect {

    private final MeterRegistry meterRegistry;

    @Around("@annotation(org.springframework.retry.annotation.Retryable)")
    public Object logRetryAttempts(ProceedingJoinPoint joinPoint) throws Throwable {
        Counter retryCounter = meterRegistry
                .counter("retry.attempts"
                        , "method"
                        , joinPoint.getSignature().getName());
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            retryCounter.increment();
            throw e;
        }
    }
}