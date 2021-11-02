package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.Usuario;
import io.github.cwireset.tcc.exception.CpfExistenteException;
import io.github.cwireset.tcc.exception.EmailExistenteException;
import io.github.cwireset.tcc.exception.UsuarioNaoExisteException;
import io.github.cwireset.tcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository repository;

    public Usuario salvar(Usuario usuario) throws EmailExistenteException, CpfExistenteException {
        if (repository.findByEmailContaining(usuario.getEmail()) != null){
            throw new EmailExistenteException(usuario.getEmail());
        }
        if (repository.findByCpfContaining(usuario.getCpf()) != null){
            throw new CpfExistenteException(usuario.getCpf());
        }
        return repository.save(usuario);

    }

    public Page<Usuario> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Usuario buscarPeloId(Long idUsuario) throws UsuarioNaoExisteException {
       if(!repository.existsById(idUsuario)){
           throw new UsuarioNaoExisteException(idUsuario);
       }
       return repository.findById(idUsuario).get();
    }
}
