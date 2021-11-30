package com.github.amanda.reservas.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImovelPossuiAnuncioException extends Exception {
    public ImovelPossuiAnuncioException() {

        super("Não é possível excluir um imóvel que possua um anúncio.");
    }
}
