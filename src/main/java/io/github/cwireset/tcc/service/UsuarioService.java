package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.Endereco;
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
        verificaAmbiguidaeEmailELancaException(usuario.getEmail(), usuario.getId());
        verificaAmbiguidadeCpfElancaException(usuario);
        return repository.save(usuario);

    }

    public Page<Usuario> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Usuario buscarPeloId(Long idUsuario) throws UsuarioIdNaoExisteException {
        verificaSeExistePeloIdELancaException(idUsuario);
        return repository.findById(idUsuario).get();
    }

    public Usuario buscaPeloCpf(String cpf) throws UsuarioCpfNaoExisteException {
        verificaSeExistePeloCpfELancaException(cpf);
        return repository.findByCpf(cpf);
    }

    public Usuario atualizar(Long id, AtualizarUsuarioRequest atualizarUsuarioRequest) throws UsuarioIdNaoExisteException, EmailJaExisteException {
        verificaSeExistePeloIdELancaException(id);

        verificaAmbiguidaeEmailELancaException(atualizarUsuarioRequest.getEmail(), id);

        Usuario usuario = buscarPeloId(id);

        if(usuario.getEndereco() == null){
            if (atualizarUsuarioRequest.getEndereco() != null){
                Endereco endereco = Endereco.builder()
                    .cep(atualizarUsuarioRequest.getEndereco().getCep())
                    .logradouro(atualizarUsuarioRequest.getEndereco().getLogradouro())
                    .numero(atualizarUsuarioRequest.getEndereco().getNumero())
                    .complemento(atualizarUsuarioRequest.getEndereco().getComplemento())
                    .bairro(atualizarUsuarioRequest.getEndereco().getBairro())
                    .cidade(atualizarUsuarioRequest.getEndereco().getCidade())
                    .estado(atualizarUsuarioRequest.getEndereco().getEstado())
                    .build();
                usuario.setEndereco(endereco);
            } else{
                usuario.setEndereco(null);
            }
        }else{
            if (atualizarUsuarioRequest.getEndereco() != null){
                Long idEndereco = usuario.getEndereco().getId();
                Endereco endereco = Endereco.builder()
                        .id(idEndereco)
                        .cep(atualizarUsuarioRequest.getEndereco().getCep())
                        .logradouro(atualizarUsuarioRequest.getEndereco().getLogradouro())
                        .numero(atualizarUsuarioRequest.getEndereco().getNumero())
                        .complemento(atualizarUsuarioRequest.getEndereco().getComplemento())
                        .bairro(atualizarUsuarioRequest.getEndereco().getBairro())
                        .cidade(atualizarUsuarioRequest.getEndereco().getCidade())
                        .estado(atualizarUsuarioRequest.getEndereco().getEstado())
                        .build();
                usuario.setEndereco(endereco);
            } else{

                usuario.setEndereco(null);
            }
        }
        usuario.setNome(atualizarUsuarioRequest.getNome());
        usuario.setEmail(atualizarUsuarioRequest.getEmail());
        usuario.setSenha(atualizarUsuarioRequest.getSenha());
        usuario.setDataNascimento(atualizarUsuarioRequest.getDataNascimento());

        repository.save(usuario);

        return usuario;
    }

    private void verificaSeExistePeloIdELancaException(Long idUsuario) throws UsuarioIdNaoExisteException {
        if(!repository.existsById(idUsuario)){
            throw new UsuarioIdNaoExisteException(idUsuario);
        }
    }

    private void verificaSeExistePeloCpfELancaException(String cpf) throws UsuarioCpfNaoExisteException {
        if(!repository.existsByCpf(cpf)){
            throw new UsuarioCpfNaoExisteException(cpf);
        }
    }

    private void verificaAmbiguidadeCpfElancaException(Usuario usuario) throws CpfJaExisteException {
        if (repository.existsByCpf(usuario.getCpf())){
            throw new CpfJaExisteException(usuario.getCpf());
        }
    }

    private void verificaAmbiguidaeEmailELancaException(String email, Long id) throws EmailJaExisteException {
        if (repository.existsByEmail(email)) {
            if (!repository.findById(id).get().getEmail().equals(email))
            throw new EmailJaExisteException(email);
        }
    }
}
