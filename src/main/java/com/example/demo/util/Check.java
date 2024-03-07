package com.example.demo.util;


import com.example.demo.exception.CustomException;
import com.example.demo.exception.StatusCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Check {
    public static void notNull(Object object , StatusCode statusCode){
        if (object==null){
            log.debug("Null Error {}", statusCode.getMessage());
            throw new CustomException(statusCode);
        }
    }

    public static void notNull(Object object , StatusCode statusCode, String logMessage){
        if (object==null){
            log.debug("Null Error {} message = {}", statusCode.getMessage(), logMessage);
            throw new CustomException(statusCode);
        }
    }
}
