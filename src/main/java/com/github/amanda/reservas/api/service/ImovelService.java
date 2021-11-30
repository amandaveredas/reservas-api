package com.github.amanda.reservas.api.service;

import com.github.amanda.reservas.api.domain.Imovel;
import com.github.amanda.reservas.api.exception.ImovelIdNaoExisteException;
import com.github.amanda.reservas.api.exception.ImovelPossuiAnuncioException;
import com.github.amanda.reservas.api.domain.Usuario;
import com.github.amanda.reservas.api.exception.UsuarioIdNaoExisteException;
import com.github.amanda.reservas.api.repository.ImovelRepository;
import com.github.amanda.reservas.api.request.CadastrarImovelRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ImovelService {

    private ImovelRepository repository;
    private UsuarioService usuarioService;
    private VerificaAnuncioPorImovelService verificaAnuncioPorImovelService;

    @Autowired
    public ImovelService(ImovelRepository repository, UsuarioService usuarioService, VerificaAnuncioPorImovelService verificaAnuncioPorImovelService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.verificaAnuncioPorImovelService = verificaAnuncioPorImovelService;
    }

    public Imovel salvar(CadastrarImovelRequest cadastrarImovelRequest) throws UsuarioIdNaoExisteException {

        Usuario proprietario = usuarioService.buscarPeloId(cadastrarImovelRequest.getIdProprietario());

        Imovel imovel = Imovel.builder()
                .identificacao(cadastrarImovelRequest.getIdentificacao())
                .tipoImovel(cadastrarImovelRequest.getTipoImovel())
                .endereco(cadastrarImovelRequest.getEndereco())
                .proprietario(proprietario)
                .caracteristicas(cadastrarImovelRequest.getCaracteristicas())
                .ativo(true)
                .build();

        return repository.save(imovel);
    }

    public Page<Imovel> buscarTodos(Pageable pageable) {

        return repository.findAllByAtivoIsTrue(pageable);
    }

    public Page<Imovel> buscarImoveisPorProprietario(Pageable pageable, Long idProprietario) {
        try {
            Usuario proprietario = usuarioService.buscarPeloId(idProprietario);
            return repository.findAllByAtivoIsTrueAndProprietarioEquals(proprietario,pageable);
        } catch (UsuarioIdNaoExisteException e) {
            return repository.findAllByProprietario(null,pageable);
        }
    }

    public Imovel buscarPeloId(Long idImovel) throws ImovelIdNaoExisteException {
        verificaSeImovelExisteELancaException(idImovel);
        return repository.findById(idImovel).get();
    }

    public void excluir(Long idImovel) throws ImovelIdNaoExisteException, ImovelPossuiAnuncioException {
        Imovel imovel = buscarPeloId(idImovel);
        if(verificaAnuncioPorImovelService.verificaSeExisteAnuncioParaImovel(imovel))
            throw new ImovelPossuiAnuncioException();
        imovel.setAtivo(false);
        repository.save(imovel);
    }

    private void verificaSeImovelExisteELancaException(Long idImovel) throws ImovelIdNaoExisteException {
        if(!repository.existsByIdAndAtivoIsTrue(idImovel)) throw new ImovelIdNaoExisteException(idImovel);
    }
}
