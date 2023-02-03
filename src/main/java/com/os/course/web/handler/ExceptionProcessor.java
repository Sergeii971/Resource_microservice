package com.os.course.web.handler;

import com.os.course.model.dto.ErrorDto;
import com.os.course.model.exception.FileNotFoundException;
import com.os.course.model.exception.IncorrectParameterValueException;
import com.os.course.model.exception.ServerErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionProcessor extends ResponseEntityExceptionHandler {
    private static final int INCORRECT_PARAMETER_VALUE_CODE = 400;
    private static final int RESOURCE_NOT_FOUND_CODE = 404;
    private static final int UNAUTHORIZED_CODE = 401;
    private static final int FORBIDDEN_CODE = 403;
    private static final int INTERNAL_SERVER_ERROR = 500;

    @ExceptionHandler(value = IncorrectParameterValueException.class)
    public ResponseEntity<ErrorDto> handleIncorrectParameterValueException(IncorrectParameterValueException exception) {
        return new ResponseEntity<>(new ErrorDto(exception.getMessage(), INCORRECT_PARAMETER_VALUE_CODE), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = FileNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFoundException(FileNotFoundException exception) {
        return new ResponseEntity<>(new ErrorDto(exception.getMessage(), RESOURCE_NOT_FOUND_CODE), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ServerErrorException.class)
    public ResponseEntity<ErrorDto> handleServerErrorException(ServerErrorException exception) {
        return new ResponseEntity<>(new ErrorDto(exception.getMessage(), INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
