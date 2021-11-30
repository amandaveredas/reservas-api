package com.github.amanda.reservas.api.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CpfJaExisteException extends Exception {
    public CpfJaExisteException(String cpf) {
        super("Já existe um recurso do tipo Usuario com CPF com o valor '"+cpf+"'.");
    }
}
