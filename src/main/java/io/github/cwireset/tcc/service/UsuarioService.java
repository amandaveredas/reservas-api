package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.Usuario;
import io.github.cwireset.tcc.exception.CpfJaExisteException;
import io.github.cwireset.tcc.exception.EmailJaExisteException;
import io.github.cwireset.tcc.exception.UsuarioCpfNaoExisteException;
import io.github.cwireset.tcc.exception.UsuarioIdNaoExisteException;
import io.github.cwireset.tcc.repository.UsuarioRepository;
import io.github.cwireset.tcc.request.AtualizarUsuarioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository repository;

    public Usuario salvar(Usuario usuario) throws EmailJaExisteException, CpfJaExisteException {
        verificaAmbiguidaeEmail(usuario.getEmail());
        verificaAmbiguidadeCpf(usuario);
        return repository.save(usuario);

    }

    public Page<Usuario> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Usuario buscarPeloId(Long idUsuario) throws UsuarioIdNaoExisteException {
        verificaSeExistePeloId(idUsuario);
        return repository.findById(idUsuario).get();
    }

    public Usuario buscaPeloCpf(String cpf) throws UsuarioCpfNaoExisteException {
        verificaSeExistePeloCpf(cpf);
        return repository.findByCpf(cpf);
    }

    public Usuario atualizar(Long id, AtualizarUsuarioRequest atualizarUsuarioRequest) throws UsuarioIdNaoExisteException, EmailJaExisteException {
        verificaSeExistePeloId(id);

        verificaAmbiguidaeEmail(atualizarUsuarioRequest.getEmail());

        Usuario usuario = buscarPeloId(id);

        usuario.setNome(atualizarUsuarioRequest.getNome());
        usuario.setEmail(atualizarUsuarioRequest.getEmail());
        usuario.setEndereco(atualizarUsuarioRequest.getEndereco());
        usuario.setSenha(atualizarUsuarioRequest.getSenha());
        usuario.setDataNascimento(atualizarUsuarioRequest.getDataNascimento());

        repository.save(usuario);

        return usuario;
    }

    private void verificaSeExistePeloId(Long idUsuario) throws UsuarioIdNaoExisteException {
        if(!repository.existsById(idUsuario)){
            throw new UsuarioIdNaoExisteException(idUsuario);
        }
    }

    private void verificaSeExistePeloCpf(String cpf) throws UsuarioCpfNaoExisteException {
        if(!repository.existsByCpf(cpf)){
            throw new UsuarioCpfNaoExisteException(cpf);
        }
    }

    private void verificaAmbiguidadeCpf(Usuario usuario) throws CpfJaExisteException {
        if (repository.existsByCpf(usuario.getCpf())){
            throw new CpfJaExisteException(usuario.getCpf());
        }
    }

    private void verificaAmbiguidaeEmail(String email) throws EmailJaExisteException {
        if (repository.existsByEmail(email)) {
            throw new EmailJaExisteException(email);
        }
    }
}
