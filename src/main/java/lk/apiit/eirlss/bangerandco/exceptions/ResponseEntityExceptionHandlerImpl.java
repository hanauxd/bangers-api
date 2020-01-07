package lk.apiit.eirlss.bangerandco.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class ResponseEntityExceptionHandlerImpl extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public final ResponseEntity<Object> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(new CustomExceptionResponse(ex.getMessage()), ex.getHttpStatus());
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return new ResponseEntity<>(new CustomExceptionResponse(ex.getMessage()), HttpStatus.FORBIDDEN);
    }
}
