package com.github.amanda.reservas.api.exception;

import com.github.amanda.reservas.api.domain.FormaPagamento;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FormaPagaMentoInvalidaException extends Exception {
    public FormaPagaMentoInvalidaException(Enum<FormaPagamento> formaPagamento, String formasAceitas) {

        super("O anúncio não aceita "+formaPagamento+" como forma de pagamento. As formas aceitas são "+formasAceitas+"."   );
    }
}
