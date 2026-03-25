package com.yunus.handler;


import com.yunus.dto.ErrorResponse;
import com.yunus.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(message, status.value()));

    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> BaseExceptionHandler(BaseException ex) {

        HttpStatus status = ex.getErrorType().getHttpStatus();
        return build(status, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        String firstError = ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();
        return build(HttpStatus.BAD_REQUEST, "Validation Error: " + firstError + "");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Global hata yakalandı: ", ex);

        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Sistemsel Bir Hata Oluştu!");
    }


}
