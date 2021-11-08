package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.Imovel;
import io.github.cwireset.tcc.domain.Usuario;
import io.github.cwireset.tcc.exception.ImovelIdNaoExisteException;
import io.github.cwireset.tcc.exception.ImovelPossuiAnuncioException;
import io.github.cwireset.tcc.exception.UsuarioIdNaoExisteException;
import io.github.cwireset.tcc.repository.ImovelRepository;
import io.github.cwireset.tcc.request.CadastrarImovelRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.ApiIgnore;

@Service
public class ImovelService {

    private ImovelRepository repository;
    private UsuarioService usuarioService;
    private AnuncioService anuncioService;

    @Autowired
    public ImovelService(ImovelRepository repository, UsuarioService usuarioService, AnuncioService anuncioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.anuncioService = anuncioService;
    }

    public Imovel salvar(CadastrarImovelRequest cadastrarImovelRequest) throws UsuarioIdNaoExisteException {

        Usuario proprietario = usuarioService.buscarPeloId(cadastrarImovelRequest.getIdProprietario());

        Imovel imovel = Imovel.builder()
                .identificacao(cadastrarImovelRequest.getIdentificacao())
                .tipoImovel(cadastrarImovelRequest.getTipoImovel())
                .endereco(cadastrarImovelRequest.getEndereco())
                .proprietario(proprietario)
                .caracteristicas(cadastrarImovelRequest.getCaracteristicas())
                .build();

        return repository.save(imovel);
    }

    public Page<Imovel> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Imovel> buscarImoveisPorProprietario(@ApiIgnore Pageable pageable, Long idProprietario) {
        Usuario proprietario = null;
        try {
            proprietario = usuarioService.buscarPeloId(idProprietario);
            return repository.findAllByProprietario(pageable, proprietario);
        } catch (UsuarioIdNaoExisteException e) {
            return repository.findAllByProprietario(pageable, null);
        }
    }

    public Imovel buscarPeloId(Long idImovel) throws ImovelIdNaoExisteException {
        verificaSeImovelExisteELancaException(idImovel);
        return repository.findById(idImovel).get();
    }

    public void excluir(Long idImovel) throws ImovelIdNaoExisteException, ImovelPossuiAnuncioException {
        Imovel imovel = buscarPeloId(idImovel);

        if (anuncioService.verificaSeExisteAnuncioPorImovel(imovel))
        throw new ImovelPossuiAnuncioException();

        repository.deleteById(idImovel);
    }

    private void verificaSeImovelExisteELancaException(Long idImovel) throws ImovelIdNaoExisteException {
        if(!repository.existsById(idImovel)) throw new ImovelIdNaoExisteException(idImovel);
    }
}
