package com.github.amanda.reservas.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NumeroMinimoPessoasException extends Exception {
    public NumeroMinimoPessoasException(Integer quantidadePessoas, String tipoImovel) {
        super("Não é possivel realizar uma reserva com menos de "+quantidadePessoas+" pessoas para imóveis do tipo "+tipoImovel+"");
    }
}
