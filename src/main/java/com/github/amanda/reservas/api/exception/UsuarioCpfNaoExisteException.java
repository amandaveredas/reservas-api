package com.github.amanda.reservas.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsuarioCpfNaoExisteException extends Exception {
    public UsuarioCpfNaoExisteException(String cpf) {
        super("Nenhum(a) Usuario com CPF com o valor '"+cpf+"' foi encontrado.");
    }
}
