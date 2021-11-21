package io.github.cwireset.tcc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReservaNaoExisteException extends Exception {
    public ReservaNaoExisteException(Long idReserva) {
        super("Nenhum(a) Reserva com Id com o valor '"+idReserva+"' foi encontrado.");
    }
}
