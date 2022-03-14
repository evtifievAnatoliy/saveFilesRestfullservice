package com.quantori.restfullservice.exeption;

public class FileSystemRepositoryException extends Exception{
    public FileSystemRepositoryException(String errorMessage) {
        super(errorMessage);
    }
}
