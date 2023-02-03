package com.os.course.model.exception;

public class IncorrectParameterValueException extends RuntimeException {
    public IncorrectParameterValueException() {
        super();
    }

    public IncorrectParameterValueException(String message) {
        super(message);
    }
}
