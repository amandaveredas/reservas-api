package io.github.cwireset.tcc.controller;

import io.github.cwireset.tcc.domain.Imovel;
import io.github.cwireset.tcc.exception.ImovelIdNaoExisteException;
import io.github.cwireset.tcc.exception.UsuarioIdNaoExisteException;
import io.github.cwireset.tcc.request.CadastrarImovelRequest;
import io.github.cwireset.tcc.service.ImovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/imoveis")
public class ImovelController {

    @Autowired
    ImovelService imovelService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Imovel cadastrar(@RequestBody @Valid CadastrarImovelRequest cadastrarImovelRequest) throws UsuarioIdNaoExisteException {
        return imovelService.salvar(cadastrarImovelRequest);
    }

    @GetMapping
    public Page<Imovel> listarTodos(@PageableDefault(
            sort = "identificacao",
            direction = Sort.Direction.ASC,
            page = 0,
            size = 10) Pageable pageable) {

        return imovelService.buscarTodos(pageable);
    }

    @GetMapping("/proprietarios/{idProprietario}")
    public Page<Imovel> listarImoveisPorProprietario (@PathVariable Long idProprietario, @PageableDefault(
            sort = "identificacao",
            direction = Sort.Direction.ASC,
            page = 0,
            size = 10) Pageable pageable) throws UsuarioIdNaoExisteException {

        return  imovelService.buscarImoveisPorProprietario(pageable, idProprietario);
    }

    @GetMapping("/{idImovel}")
    public Imovel buscarPeloId(@PathVariable Long idImovel) throws ImovelIdNaoExisteException {
        return imovelService.buscarPeloId(idImovel);
    }
}
