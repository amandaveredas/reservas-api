package io.github.cwireset.tcc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsuarioCpfNaoExisteException extends Exception {
    public UsuarioCpfNaoExisteException(String cpf) {
        super("Nenhum(a) Usuario com CPF com o valor '"+cpf+"' foi encontrado.");
    }
}
