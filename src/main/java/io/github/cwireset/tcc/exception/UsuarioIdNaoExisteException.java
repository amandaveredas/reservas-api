package io.github.cwireset.tcc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsuarioIdNaoExisteException extends Exception {
    public UsuarioIdNaoExisteException(Long idUsuario) {
        super("Nenhum(a) Usuario com Id com o valor '"+idUsuario+"' foi encontrado.");
    }
}
