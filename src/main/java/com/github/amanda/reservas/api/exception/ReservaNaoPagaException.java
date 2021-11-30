package com.github.amanda.reservas.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReservaNaoPagaException extends Exception {
    public ReservaNaoPagaException() {
        super("Não é possível realizar o estorno para esta reserva, pois ela não está no status PAGO.");
    }
}
