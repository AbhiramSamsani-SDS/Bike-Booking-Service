package com.bikebookingservice.controller;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import com.bikebookingservice.exception.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
    private ResponseEntity<ErrorDetails> response(Exception e, WebRequest request, HttpStatus status) {
        return new ResponseEntity<>(new ErrorDetails(new Date(), e.getMessage(), request.getDescription(false)), status);
    }
    
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ErrorDetails> notFound(RecordNotFoundException e, WebRequest r){
    	return response(e,r,HttpStatus.NOT_FOUND); 
    }
    
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorDetails> badRequest(InsufficientBalanceException e, WebRequest r){
    	return response(e,r,HttpStatus.BAD_REQUEST); 
    }
    
    @ExceptionHandler(DriversNotAvailableException.class)
    public ResponseEntity<ErrorDetails> unavailable(DriversNotAvailableException e, WebRequest r){
    	return response(e,r,HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> conflict(EmailAlreadyExistsException e, WebRequest r){
    	return response(e,r,HttpStatus.CONFLICT); 
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> illegal(IllegalArgumentException e, WebRequest r){
    	return response(e,r,HttpStatus.BAD_REQUEST); 
    }
}
