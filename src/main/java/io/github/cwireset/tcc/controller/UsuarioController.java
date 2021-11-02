package io.github.cwireset.tcc.controller;

import io.github.cwireset.tcc.domain.Usuario;
import io.github.cwireset.tcc.exception.CpfExistenteException;
import io.github.cwireset.tcc.exception.EmailExistenteException;
import io.github.cwireset.tcc.exception.UsuarioNaoExisteException;
import io.github.cwireset.tcc.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario cadastrar(@RequestBody @Valid Usuario usuario) throws EmailExistenteException, CpfExistenteException {
        return usuarioService.salvar(usuario);

    }

    @GetMapping
    public Page<Usuario> listarTodos(@PageableDefault(
            sort = "nome",
            direction = Sort.Direction.ASC,
            page = 0,
            size = 10) Pageable pageable) {

        return usuarioService.buscarTodos(pageable);
    }

    @GetMapping("/{idUsuario}")
    public Usuario buscarPeloId(@PathVariable Long idUsuario) throws UsuarioNaoExisteException {
        return usuarioService.buscarPeloId(idUsuario);
    }


}
