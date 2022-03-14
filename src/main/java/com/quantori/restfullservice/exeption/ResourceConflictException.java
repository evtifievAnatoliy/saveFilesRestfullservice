package com.quantori.restfullservice.exeption;

public class ResourceConflictException extends Exception{
    public ResourceConflictException(String errorMessage) {
        super(errorMessage);
    }
}
