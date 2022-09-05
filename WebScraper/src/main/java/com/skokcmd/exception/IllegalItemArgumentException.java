package com.skokcmd.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IllegalItemArgumentException extends IllegalArgumentException {

    public IllegalItemArgumentException() {
        super();
    }

    public IllegalItemArgumentException(String message) {
        super(message);
    }
}
