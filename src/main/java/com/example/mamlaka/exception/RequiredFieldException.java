package com.example.mamlaka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RequiredFieldException extends RuntimeException {

    public RequiredFieldException(String resourceName, String fieldName) {
        super(String.format("%s is required for %s", resourceName, fieldName));
    }

}
