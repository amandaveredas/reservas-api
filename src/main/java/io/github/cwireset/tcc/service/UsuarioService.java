package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.Usuario;
import io.github.cwireset.tcc.exception.CpfExistenteException;
import io.github.cwireset.tcc.exception.EmailExistenteException;
import io.github.cwireset.tcc.exception.UsuarioCpfNaoExisteException;
import io.github.cwireset.tcc.exception.UsuarioIdNaoExisteException;
import io.github.cwireset.tcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository repository;

    public Usuario salvar(Usuario usuario) throws EmailExistenteException, CpfExistenteException {
        if (repository.findByEmail(usuario.getEmail()) != null){
            throw new EmailExistenteException(usuario.getEmail());
        }
        if (repository.findByCpf(usuario.getCpf()) != null){
            throw new CpfExistenteException(usuario.getCpf());
        }
        return repository.save(usuario);

    }

    public Page<Usuario> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Usuario buscarPeloId(Long idUsuario) throws UsuarioIdNaoExisteException {
       if(!repository.existsById(idUsuario)){
           throw new UsuarioIdNaoExisteException(idUsuario);
       }
       return repository.findById(idUsuario).get();
    }

    public Usuario buscaPeloCpf(String cpf) throws UsuarioCpfNaoExisteException {
        if(!repository.existsByCpf(cpf)){
            throw new UsuarioCpfNaoExisteException(cpf);
        }
        return repository.findByCpf(cpf);
    }
}
