package com.github.amanda.reservas.api.service;

import com.github.amanda.reservas.api.domain.Usuario;
import com.github.amanda.reservas.api.repository.UsuarioRepository;
import com.github.amanda.reservas.api.domain.Endereco;
import com.github.amanda.reservas.api.exception.CpfJaExisteException;
import com.github.amanda.reservas.api.exception.EmailJaExisteException;
import com.github.amanda.reservas.api.exception.UsuarioCpfNaoExisteException;
import com.github.amanda.reservas.api.exception.UsuarioIdNaoExisteException;
import com.github.amanda.reservas.api.request.AtualizarUsuarioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private UsuarioRepository repository;
    private ImagemService imagemService;

    @Autowired
    public UsuarioService(UsuarioRepository repository, ImagemService imagemService) {
        this.repository = repository;
        this.imagemService = imagemService;
    }

    public Usuario salvar(Usuario usuario) throws EmailJaExisteException, CpfJaExisteException {
        verificaAmbiguidadeEmailAoCadastrarELancaException(usuario.getEmail());
        verificaAmbiguidadeCpfElancaException(usuario);
        //usuario.setAvatar(imagemService.obterImagem().getLink());
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
        Usuario usuario = buscarPeloId(id);

        verificaAmbiguidadeEmailAoAtualizarELancaException(atualizarUsuarioRequest.getEmail(), id);

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

    private void verificaAmbiguidadeEmailAoCadastrarELancaException(String email) throws EmailJaExisteException {
        if (repository.existsByEmail(email)) {
            throw new EmailJaExisteException(email);
        }
    }

    private void verificaAmbiguidadeEmailAoAtualizarELancaException(String email, Long id) throws EmailJaExisteException {
        if (repository.existsByEmail(email)) {
            Usuario usuario = repository.findById(id).get();
            if (!usuario.getEmail().equals(email))
            throw new EmailJaExisteException(email);
        }
    }
}
