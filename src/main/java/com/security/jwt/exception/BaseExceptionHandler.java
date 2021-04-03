package com.security.jwt.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDeniedExceptionHandler(){
        return "/error/403";
    }

}
