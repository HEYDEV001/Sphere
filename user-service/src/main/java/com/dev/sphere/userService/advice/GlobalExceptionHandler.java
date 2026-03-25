package com.dev.sphere.userService.advice;

import com.dev.sphere.userService.exception.BadRequestException;
import com.dev.sphere.userService.exception.ResourceNotFoundException;
import com.dev.sphere.userService.exception.UnAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.sasl.AuthenticationException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException e) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getLocalizedMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequest(BadRequestException e) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getLocalizedMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(RuntimeException e) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.getLocalizedMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }


    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(UnAuthorizedException e) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(e.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

//    @ExceptionHandler(JwtException.class)
//    public ResponseEntity<ApiResponse<?>> handleJwtException(JwtException e) {
//        ApiError apiError = ApiError.builder()
//                .status(HttpStatus.UNAUTHORIZED)
//                .message(e.getMessage())
//                .build();
//        return buildErrorResponseEntity(apiError);
//    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(e.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }
}
