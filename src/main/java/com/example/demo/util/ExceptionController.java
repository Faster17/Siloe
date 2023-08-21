package com.example.demo.util;

import java.nio.file.AccessDeniedException;

import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleError404(HttpServletRequest request, Exception e) {
        return new ModelAndView("errores/404");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handleError400(HttpServletRequest request, Exception e) {
        return new ModelAndView("errores/400");
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ModelAndView handleError406(HttpServletRequest request, Exception e) {
        return new ModelAndView("errores/406");
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ModelAndView handleError500(HttpServletRequest request, Exception e) {
        return new ModelAndView("errores/500");
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleError403(HttpServletRequest request, Exception e) {
        return new ModelAndView("errores/403");
    }
}



