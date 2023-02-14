package com.os.course.model.exception;

public class KafkaProducingException extends RuntimeException {
    public KafkaProducingException() {
        super();
    }

    public KafkaProducingException(String message) {
        super(message);
    }
}
