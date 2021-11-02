package io.github.cwireset.tcc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsuarioNaoExisteException extends Exception {
    public UsuarioNaoExisteException(Long idUsuario) {
        super("Nenhum(a) Usuario com Id com o valor '"+idUsuario+"' foi encontrado.");
    }
}
