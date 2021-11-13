package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.Imovel;
import io.github.cwireset.tcc.exception.ImovelAmbiguidadeAnunciosException;
import io.github.cwireset.tcc.exception.ImovelPossuiAnuncioException;
import io.github.cwireset.tcc.repository.AnuncioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificaAnuncioPorImovelService {

    private AnuncioRepository repository;

    @Autowired
    public VerificaAnuncioPorImovelService(AnuncioRepository repository) {
        this.repository = repository;
    }

    public void verificaSeImovelPodeSerExcluidoELancaExcecao(Imovel imovel) throws ImovelPossuiAnuncioException {
        if (repository.existsByImovelAndAtivoIsTrue(imovel))
            throw new ImovelPossuiAnuncioException();
    }


    public void verificaSeExisteAnuncioParaImovelELancaExcecao(Imovel imovel, Long idImovel) throws ImovelAmbiguidadeAnunciosException {
        if (repository.existsByImovelAndAtivoIsTrue(imovel))
            throw new ImovelAmbiguidadeAnunciosException(idImovel);
    }
}
