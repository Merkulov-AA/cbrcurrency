package ru.andreymerkulov.cbrcurrency.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@AllArgsConstructor
public class MethodExecutionTimeMetricsAspect {

    private final MeterRegistry meterRegistry;

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.nanoTime();
            String metricName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + ".executionTime";
            Timer.builder(metricName)
                    .description("Time taken to execute the method")
                    .register(meterRegistry)
                    .record(endTime - startTime, TimeUnit.NANOSECONDS);
        }
    }
}
