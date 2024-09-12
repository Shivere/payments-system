package com.example.mamlaka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PhoneNumberRequiredException extends RuntimeException {

    public PhoneNumberRequiredException(String message) {
        super(message);
    }

}
