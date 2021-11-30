package com.github.amanda.reservas.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AnuncioNaoExisteException extends Exception {
    public AnuncioNaoExisteException(Long idAnuncio) {
        super("Nenhum(a) Anuncio com Id com o valor '"+idAnuncio+"' foi encontrado.");
    }
}
