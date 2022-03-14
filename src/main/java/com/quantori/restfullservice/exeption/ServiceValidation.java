package com.quantori.restfullservice.exeption;

import com.quantori.restfullservice.controllers.UsersController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ServiceValidation extends ResponseEntityExceptionHandler {

    final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException arguments, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> validationMessages = new ArrayList<>();

        BindingResult bindingResult = arguments.getBindingResult();
        List<ObjectError> objectErrors = bindingResult.getAllErrors();

        for (ObjectError objectError: objectErrors) {
            String defaultMessage = objectError.getDefaultMessage();
            validationMessages.add(defaultMessage);
        }
        return new ResponseEntity<>(validationMessages, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (ResourceNotFoundException.class)
    public final ResponseEntity<Object> resourceNotFound(ResourceNotFoundException ex, WebRequest request) throws Exception{
        logger.warn(ex.getMessage());
        return  new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler (BadRequestException.class)
    public final ResponseEntity<Object> badRequest(BadRequestException ex, WebRequest request) throws Exception{
        logger.warn(ex.getMessage());
        return  new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (ResourceConflictException.class)
    public final ResponseEntity<Object> resourceConflict(ResourceConflictException ex, WebRequest request) throws Exception{
        logger.warn(ex.getMessage());
        return  new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler (FileSystemRepositoryException.class)
    public final ResponseEntity<Object> resourceConflict(FileSystemRepositoryException ex, WebRequest request) throws Exception{
        logger.warn(ex.getMessage());
        return  new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

}
