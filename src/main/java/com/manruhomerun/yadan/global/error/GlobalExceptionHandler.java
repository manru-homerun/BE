package com.manruhomerun.yadan.global.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.manruhomerun.yadan.global.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage() == null ? "잘못된 요청입니다." : fieldError.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        "COMMON_400_VALIDATION",
                        message,
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(
            BaseException exception,
            HttpServletRequest request
    ) {
        BaseErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(
                        errorCode,
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }
}
