package io.github.cwireset.tcc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImovelAmbiguidadeAnunciosException extends Exception {
    public ImovelAmbiguidadeAnunciosException(Long idImovel) {
        super("Já existe um recurso do tipo Anuncio com IdImovel com o valor '"+idImovel+"'.");
    }
}
