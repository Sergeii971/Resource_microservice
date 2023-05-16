package com.os.course.model.exception;

public class KafkaCustomException extends RuntimeException {
    public KafkaCustomException() {
        super();
    }

    public KafkaCustomException(String message) {
        super(message);
    }
}
