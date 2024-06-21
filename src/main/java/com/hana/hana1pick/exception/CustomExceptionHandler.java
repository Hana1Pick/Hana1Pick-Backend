package com.hana.hana1pick.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    protected ResponseEntity<CustomException> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
//        CustomException customErrorMessage = new CustomException(BaseResponseStatus.SUCCESS);
//        return new ResponseEntity<>(customErrorMessage, HttpStatus.ACCEPTED);
//    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<BaseResponseStatus> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(ex.getStatus(), HttpStatus.valueOf(ex.getStatus().getCode()));
    }
}
