package com.example.volunteer_system.hander;

import com.example.volunteer_system.exception.BaseBusinessException;
import com.example.volunteer_system.result.Result;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseBusinessException.class)
    public Result<Void> handleBaseBusinessException(BaseBusinessException e){
        log.error("业务异常，code: {}, 原因：{}", e.getCode(), e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e){
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg=(fieldError!=null)?fieldError.getDefaultMessage():"参数校验失败";
        log.warn("参数校验失败：{}",msg);
        return Result.error(400,msg);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e){
        String msg=e.getConstraintViolations().stream()
                .findFirst()
                .map(jakarta.validation.ConstraintViolation::getMessage)
                .orElse("参数校验失败");
        log.warn("参数校验失败：{}",msg);
        return Result.error(400,msg);
    }
    //兜底异常处理
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleGlobalException(Exception e) {
        log.error("系统内部异常: ", e);
        return Result.error(500, "系统繁忙，请稍后重试");
    }
}
