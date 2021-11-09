package io.github.cwireset.tcc.controller;

import io.github.cwireset.tcc.domain.Anuncio;
import io.github.cwireset.tcc.exception.AnuncioNaoExisteException;
import io.github.cwireset.tcc.exception.ImovelIdNaoExisteException;
import io.github.cwireset.tcc.exception.ImovelAmbiguidadeAnunciosException;
import io.github.cwireset.tcc.exception.UsuarioIdNaoExisteException;
import io.github.cwireset.tcc.request.CadastrarAnuncioRequest;
import io.github.cwireset.tcc.service.AnuncioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/anuncios")
public class AnuncioController {

    private AnuncioService anuncioService;

    @Autowired
    public AnuncioController(AnuncioService anuncioService) {
        this.anuncioService = anuncioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Anuncio cadastrarAnuncio(@RequestBody @Valid CadastrarAnuncioRequest cadastrarAnuncioRequest) throws ImovelIdNaoExisteException, UsuarioIdNaoExisteException, ImovelAmbiguidadeAnunciosException {
        return anuncioService.cadastrar(cadastrarAnuncioRequest);
    }

    @GetMapping
    public Page<Anuncio> listarTodosAnuncios(@PageableDefault(
            sort = "valorDiaria",
            direction = Sort.Direction.ASC) @ApiIgnore Pageable pageable) {

        return anuncioService.listarTodos(pageable);
    }

    @GetMapping("/anunciantes/{idAnunciante}")
    public Page<Anuncio> listarAnunciosPorAnunciante(@PathVariable Long idAnunciante, @PageableDefault(
            sort = "valorDiaria",
            direction = Sort.Direction.ASC) @ApiIgnore Pageable pageable) {

        return anuncioService.buscarAnunciosPorAnunciante(idAnunciante, pageable);
    }

    @DeleteMapping("/{idAnuncio}")
    public void excluirAnuncioPeloId(@PathVariable Long idAnuncio) throws AnuncioNaoExisteException {
        anuncioService.excluirLogicamente(idAnuncio);
    }



}
