package com.github.amanda.reservas.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NumeroMinimoDiariasException extends Exception {
    public NumeroMinimoDiariasException(long quantDiarias, String tipoImovel) {
        super("Não é possivel realizar uma reserva com menos de "+quantDiarias+" diárias para imóveis do tipo "+tipoImovel+"");
    }
}
