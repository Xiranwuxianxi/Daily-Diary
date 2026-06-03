package com.diary.config;

import com.diary.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理所有Controller抛出的异常,并记录详细错误信息
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R<?> handleRuntimeException(RuntimeException e) {
        log.error("业务异常:", e);
        return R.failed(e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public R<?> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("参数校验失败: {}", e.getMessage());
        return R.failed("参数错误: " + e.getMessage());
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public R<?> handleAccessDenied(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return R.failed("权限不足,无法访问该资源");
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public R<?> handleNullPointer(NullPointerException e) {
        log.error("空指针异常:", e);
        return R.failed("系统内部错误,请联系管理员");
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        log.error("系统异常:", e);
        return R.failed("系统异常: " + e.getMessage());
    }
}
