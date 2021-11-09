package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.Imovel;
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

    public boolean verificaSeExisteAnuncioPorImovel(Imovel imovel){
        return (repository.existsByImovel(imovel));
    }
}
