package io.github.cwireset.tcc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ImovelIdNaoExisteException extends Exception {
    public ImovelIdNaoExisteException(Long idImovel) {
        super("Nenhum(a) Imovel com Id com o valor '"+idImovel+"' foi encontrado.");
    }
}
