package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.KafkaClientProducer;

import java.util.Arrays;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class MetricAspect {
    private final KafkaClientProducer kafkaClientProducer;

    @Value("${metric.execution.time.threshold}")
    private long executionTimeThreshold;

    @Around("@annotation(ru.t1.java.demo.aop.Metric)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - startTime;

        if (executionTime > executionTimeThreshold) {
            String methodName = joinPoint.getSignature().getName();
            String parameters = joinPoint.getArgs().length > 0 ? String.join(", ",
                    Arrays.stream(joinPoint.getArgs()).map(Object::toString).toArray(String[]::new)) : "none";

            String message = String.format("Method %s executed in %d ms with parameters: %s",
                    methodName, executionTime, parameters);

            kafkaClientProducer.sendTo("t1_demo_metric_trace", message);
            log.warn("Execution time exceeded threshold: {}", message);
        }

        return result;
    }
}
