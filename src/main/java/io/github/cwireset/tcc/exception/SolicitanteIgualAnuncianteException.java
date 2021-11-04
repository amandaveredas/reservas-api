package io.github.cwireset.tcc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SolicitanteIgualAnuncianteException extends Exception {
    public SolicitanteIgualAnuncianteException() {
        super("O solicitante de uma reserva não pode ser o próprio anunciante.");
    }
}
