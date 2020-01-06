package lk.apiit.eirlss.bangerandco.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class ResponseEntityExceptionHandlerImpl extends ResponseEntityExceptionHandler {
    @ExceptionHandler
    public final ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return exceptionResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException exception) {
        return exceptionResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleJsonWebTokenException(JsonWebTokenException exception) {
        return exceptionResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> exceptionResponse(String message, HttpStatus httpStatus) {
        ExceptionResponse response = new ExceptionResponse(message);
        return new ResponseEntity<>(response, httpStatus);
    }
}
