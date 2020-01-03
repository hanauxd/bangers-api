package lk.apiit.eirlss.bangerandco.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EmailAlreadyExistExceptionResponse {
    private String email;
}
