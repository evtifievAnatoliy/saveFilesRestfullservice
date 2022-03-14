package com.quantori.restfullservice.exeption;

public class BadRequestException extends Exception{
    public BadRequestException(String errorMessage) {
        super(errorMessage);
    }
}
