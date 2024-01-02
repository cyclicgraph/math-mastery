package com.cyclicgraph.masterymath.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException e) {
        // this AccessDeniedException is not thrown in filter chain flow but from AuthorizationManagerBeforeMethodInterceptor as stated in https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html
        // therefore we handle it here

        log.error(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "insufficient permission for resource access");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
