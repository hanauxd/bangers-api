package lk.apiit.eirlss.bangerandco.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JsonWebTokenException extends RuntimeException {
    public JsonWebTokenException(String message) {
        super(message);
    }
}
