package com.os.course.model.exception;

public class MultipartFileException extends RuntimeException {
    public MultipartFileException() {
        super();
    }

    public MultipartFileException(String message) {
        super(message);
    }
}
