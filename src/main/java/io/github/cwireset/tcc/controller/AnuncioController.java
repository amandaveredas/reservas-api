package io.github.cwireset.tcc.controller;

import io.github.cwireset.tcc.domain.Anuncio;
import io.github.cwireset.tcc.exception.ImovelIdNaoExisteException;
import io.github.cwireset.tcc.exception.ImovelAmbiguidadeAnunciosException;
import io.github.cwireset.tcc.exception.UsuarioIdNaoExisteException;
import io.github.cwireset.tcc.request.CadastrarAnuncioRequest;
import io.github.cwireset.tcc.service.AnuncioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/anuncios")
public class AnuncioController {

    @Autowired
    AnuncioService anuncioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Anuncio cadastrarAnuncio(@RequestBody @Valid CadastrarAnuncioRequest cadastrarAnuncioRequest) throws ImovelIdNaoExisteException, UsuarioIdNaoExisteException, ImovelAmbiguidadeAnunciosException {
        return anuncioService.cadastrar(cadastrarAnuncioRequest);
    }

}