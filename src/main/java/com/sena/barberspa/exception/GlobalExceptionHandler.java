package com.sena.barberspa.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.thymeleaf.exceptions.TemplateInputException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ModelAndView handleOAuth2AuthenticationException(OAuth2AuthenticationException ex, HttpServletRequest request) {
        OAuth2Error error = ex.getError();
        logger.error("OAuth2 authentication error: {} - {}", error.getErrorCode(), error.getDescription());

        ModelAndView mav = new ModelAndView();
        mav.addObject("errorCode", error.getErrorCode());
        mav.addObject("errorDescription", error.getDescription());
        mav.addObject("requestUri", request.getRequestURI());
        mav.setViewName("error/oauth2_error");
        return mav;
    }

    @ExceptionHandler(TemplateInputException.class)
    public ModelAndView handleTemplateInputException(TemplateInputException ex, HttpServletRequest request) {
        logger.error("Thymeleaf template error: {} - URI: {}", ex.getMessage(), request.getRequestURI());

        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", "Error en la plantilla: " + ex.getMessage());
        mav.addObject("requestUri", request.getRequestURI());
        mav.addObject("errorType", "Template Error");
        mav.setViewName("error/error");
        return mav;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        logger.error("No handler found for {} {} - URI: {}", ex.getHttpMethod(), ex.getRequestURL(), request.getRequestURI());

        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", "Página no encontrada: " + ex.getRequestURL());
        mav.addObject("requestUri", request.getRequestURI());
        mav.addObject("errorType", "404 Not Found");
        mav.setViewName("error/error");
        return mav;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        logger.error("Method not supported: {} {} - URI: {}", ex.getMethod(), request.getRequestURI(), ex.getMessage());

        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", "Método no soportado: " + ex.getMethod());
        mav.addObject("requestUri", request.getRequestURI());
        mav.addObject("errorType", "Method Not Allowed");
        mav.setViewName("error/error");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error: {} - URI: {}", ex.getMessage(), request.getRequestURI(), ex);

        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", "Error inesperado: " + ex.getMessage());
        mav.addObject("requestUri", request.getRequestURI());
        mav.addObject("errorType", "Internal Server Error");
        mav.setViewName("error/error");
        return mav;
    }
}