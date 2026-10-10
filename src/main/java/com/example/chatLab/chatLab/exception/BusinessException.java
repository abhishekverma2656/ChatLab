package com.example.chatLab.chatLab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class BusinessException extends RuntimeException{
    public BusinessException (String message){
        super(message);
    }
}
