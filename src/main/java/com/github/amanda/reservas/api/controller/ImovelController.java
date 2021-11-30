package com.github.amanda.reservas.api.controller;

import com.github.amanda.reservas.api.domain.Imovel;
import com.github.amanda.reservas.api.exception.ImovelIdNaoExisteException;
import com.github.amanda.reservas.api.exception.ImovelPossuiAnuncioException;
import com.github.amanda.reservas.api.exception.UsuarioIdNaoExisteException;
import com.github.amanda.reservas.api.service.ImovelService;
import com.github.amanda.reservas.api.request.CadastrarImovelRequest;
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
@RequestMapping("/imoveis")
public class ImovelController {


    private ImovelService imovelService;

    @Autowired
    public ImovelController(ImovelService imovelService) {
        this.imovelService = imovelService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Imovel cadastrar(@RequestBody @Valid CadastrarImovelRequest cadastrarImovelRequest) throws UsuarioIdNaoExisteException {
        return imovelService.salvar(cadastrarImovelRequest);
    }

    @GetMapping
    public Page<Imovel> listarTodos(@PageableDefault(
            sort = "identificacao",
            direction = Sort.Direction.ASC) @ApiIgnore Pageable pageable) {

        return imovelService.buscarTodos(pageable);
    }

    @GetMapping("/proprietarios/{idProprietario}")
    public Page<Imovel> listarImoveisPorProprietario (@PathVariable Long idProprietario, @PageableDefault(
            sort = "identificacao",
            direction = Sort.Direction.ASC,
            page = 0,
            size = 10) @ApiIgnore Pageable pageable) throws UsuarioIdNaoExisteException {

        return  imovelService.buscarImoveisPorProprietario(pageable, idProprietario);
    }

    @GetMapping("/{idImovel}")
    public Imovel buscarPeloId(@PathVariable Long idImovel) throws ImovelIdNaoExisteException {
        return imovelService.buscarPeloId(idImovel);
    }

    @DeleteMapping("/{idImovel}")
    public void excluirImovelPeloId(@PathVariable Long idImovel) throws ImovelIdNaoExisteException, ImovelPossuiAnuncioException {
        imovelService.excluir(idImovel);
    }
}
