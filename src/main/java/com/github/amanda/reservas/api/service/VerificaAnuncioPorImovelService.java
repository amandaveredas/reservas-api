package com.github.amanda.reservas.api.service;

import com.github.amanda.reservas.api.domain.Imovel;
import com.github.amanda.reservas.api.repository.AnuncioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificaAnuncioPorImovelService {

    private AnuncioRepository repository;

    @Autowired
    public VerificaAnuncioPorImovelService(AnuncioRepository repository) {
        this.repository = repository;
    }

    public boolean verificaSeExisteAnuncioParaImovel(Imovel imovel) {
        if (repository.existsByImovelAndAtivoIsTrue(imovel)) return true;
        else return false;

    }
}
