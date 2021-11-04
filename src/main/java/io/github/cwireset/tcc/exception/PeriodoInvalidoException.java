package io.github.cwireset.tcc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PeriodoInvalidoException extends Exception {
    public PeriodoInvalidoException(String message) {
        super(message);
    }
}
