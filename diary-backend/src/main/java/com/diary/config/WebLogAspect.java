package com.diary.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.diary.common.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Controller 请求响应日志切面
 * 自动记录所有Controller方法的请求参数、响应结果和执行时间
 */
@Slf4j
@Aspect
@Component
public class WebLogAspect {

    private final ObjectMapper objectMapper;

    public WebLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Around("execution(* com.diary.controller..*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // 获取当前用户ID
            Object userId = null;
            try {
                userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            } catch (Exception e) {
                // 未登录用户,忽略
            }

            // 记录请求信息
            log.info("========================================");
            log.info("请求地址: {} {}", request.getMethod(), request.getRequestURI());
            log.info("用户ID: {}", userId != null ? userId : "未登录");
            log.info("请求参数: {}", getParams(point));
            log.info("========================================");
        }

        // 执行目标方法
        Object result = null;
        try {
            result = point.proceed();
            return result;
        } finally {
            // 计算执行时间并记录响应
            long elapsedTime = System.currentTimeMillis() - startTime;

            log.info("----------------------------------------");
            log.info("响应结果 (耗时: {}ms):", elapsedTime);

            if (result instanceof R) {
                try {
                    String json = objectMapper.writeValueAsString(result);
                    log.info(json);
                } catch (Exception e) {
                    log.warn("响应结果序列化失败: {}", e.getMessage());
                    log.info("响应对象: {}", result);
                }
            } else {
                log.info("响应对象: {}", result);
            }
            log.info("----------------------------------------\n");
        }
    }

    /**
     * 获取请求参数
     */
    private String getParams(ProceedingJoinPoint point) {
        try {
            Object[] args = point.getArgs();
            if (args == null || args.length == 0) {
                return "无参数";
            }

            // 过滤掉HttpServletRequest等不需要序列化的对象
            StringBuilder sb = new StringBuilder();
            String[] argNames = getArgNames(point);
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];

                // 跳过Spring原生对象
                if (arg instanceof HttpServletRequest) {
                    continue;
                }

                try {
                    String json = objectMapper.writeValueAsString(arg);
                    sb.append(argNames != null && i < argNames.length ? argNames[i] : "arg" + i)
                      .append("=")
                      .append(json)
                      .append(" ");
                } catch (Exception e) {
                    sb.append(argNames != null && i < argNames.length ? argNames[i] : "arg" + i)
                      .append("=")
                      .append(arg.toString())
                      .append(" ");
                }
            }
            return sb.toString().trim();
        } catch (Exception e) {
            return "参数解析失败: " + e.getMessage();
        }
    }

    /**
     * 尝试获取参数名(需要-java8参数名编译选项)
     */
    private String[] getArgNames(ProceedingJoinPoint point) {
        try {
            return ((org.aspectj.lang.reflect.MethodSignature) point.getSignature()).getParameterNames();
        } catch (Exception e) {
            return null;
        }
    }
}
