package com.github.amanda.reservas.api.controller;

import com.github.amanda.reservas.api.domain.Usuario;
import com.github.amanda.reservas.api.exception.UsuarioIdNaoExisteException;
import com.github.amanda.reservas.api.service.UsuarioService;
import com.github.amanda.reservas.api.exception.CpfJaExisteException;
import com.github.amanda.reservas.api.exception.EmailJaExisteException;
import com.github.amanda.reservas.api.exception.UsuarioCpfNaoExisteException;
import com.github.amanda.reservas.api.request.AtualizarUsuarioRequest;
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
@RequestMapping("/usuarios")
public class UsuarioController {


    private UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario cadastrar(@RequestBody @Valid Usuario usuario) throws EmailJaExisteException, CpfJaExisteException {
        return usuarioService.salvar(usuario);

    }

    @GetMapping
    public Page<Usuario> listarTodos(@PageableDefault(
            sort = "nome",
            direction = Sort.Direction.ASC) @ApiIgnore Pageable pageable) {

        return usuarioService.buscarTodos(pageable);
    }

    @GetMapping("/{idUsuario}")
    public Usuario buscarPeloId(@PathVariable Long idUsuario) throws UsuarioIdNaoExisteException {
        return usuarioService.buscarPeloId(idUsuario);
    }

    @GetMapping("/cpf/{cpf}")
    public Usuario buscarPeloCpf(@PathVariable String cpf) throws UsuarioCpfNaoExisteException {
        return usuarioService.buscaPeloCpf(cpf);
    }

    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable Long id, @RequestBody @Valid AtualizarUsuarioRequest atualizarUsuarioRequest) throws UsuarioIdNaoExisteException, EmailJaExisteException {
        return usuarioService.atualizar(id, atualizarUsuarioRequest);
    }






}
