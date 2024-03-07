package com.example.demo.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    StatusCode statusCode;

    public CustomException(StatusCode statusCode){
        this.statusCode = statusCode;
    }
}
