package com.github.amanda.reservas.api.service;

import com.github.amanda.reservas.api.domain.Anuncio;
import com.github.amanda.reservas.api.domain.Imovel;
import com.github.amanda.reservas.api.domain.Usuario;
import com.github.amanda.reservas.api.exception.AnuncioNaoExisteException;
import com.github.amanda.reservas.api.exception.ImovelAmbiguidadeAnunciosException;
import com.github.amanda.reservas.api.exception.ImovelIdNaoExisteException;
import com.github.amanda.reservas.api.exception.UsuarioIdNaoExisteException;
import com.github.amanda.reservas.api.repository.AnuncioRepository;
import com.github.amanda.reservas.api.request.CadastrarAnuncioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AnuncioService {

    private AnuncioRepository repository;
    private ImovelService imovelService;
    private UsuarioService usuarioService;
    private VerificaAnuncioPorImovelService verificaAnuncioPorImovelService;

    @Autowired
    public AnuncioService(AnuncioRepository repository, ImovelService imovelService, UsuarioService usuarioService, VerificaAnuncioPorImovelService verificaAnuncioPorImovelService) {
        this.repository = repository;
        this.imovelService = imovelService;
        this.usuarioService = usuarioService;
        this.verificaAnuncioPorImovelService = verificaAnuncioPorImovelService;
    }

    public Anuncio cadastrar(CadastrarAnuncioRequest cadastrarAnuncioRequest) throws ImovelIdNaoExisteException, UsuarioIdNaoExisteException, ImovelAmbiguidadeAnunciosException {

        Long idImovel = cadastrarAnuncioRequest.getIdImovel();
        Imovel imovel = imovelService.buscarPeloId(idImovel);

        Usuario anunciante = usuarioService.buscarPeloId(cadastrarAnuncioRequest.getIdAnunciante());

        if(verificaAnuncioPorImovelService.verificaSeExisteAnuncioParaImovel(imovel))
            throw new ImovelAmbiguidadeAnunciosException(idImovel);

        Anuncio anuncio = Anuncio.builder()
                .tipoAnuncio(cadastrarAnuncioRequest.getTipoAnuncio())
                .imovel(imovel)
                .anunciante(anunciante)
                .valorDiaria(cadastrarAnuncioRequest.getValorDiaria())
                .formasAceitas(cadastrarAnuncioRequest.getFormasAceitas())
                .descricao(cadastrarAnuncioRequest.getDescricao())
                .ativo(true)
                .build();

        return repository.save(anuncio);
    }


    public Page<Anuncio> listarTodos(Pageable pageable) {
        return repository.findAllByAtivoIsTrue(pageable);
    }

    public Page<Anuncio> buscarAnunciosPorAnunciante(Long idAnunciante, Pageable pageable) {
        try {
            Usuario anunciante =  usuarioService.buscarPeloId(idAnunciante);
            return repository.findAllByAtivoIsTrueAndAndAnuncianteEquals(anunciante,pageable);
        } catch (UsuarioIdNaoExisteException e) {
            return repository.findAllByAtivoIsTrueAndAndAnuncianteEquals(null,pageable);
        }
    }

    public void excluirLogicamente(Long idAnuncio) throws AnuncioNaoExisteException {
        Anuncio anuncio = buscarPeloId(idAnuncio);
        anuncio.setAtivo(false);
        repository.save(anuncio);
    }

    public Anuncio buscarPeloId(Long idAnuncio) throws AnuncioNaoExisteException {
        return buscaPeloIdELancaExcecao(idAnuncio);
    }

    private Anuncio buscaPeloIdELancaExcecao(Long idAnuncio) throws AnuncioNaoExisteException {
        if(!repository.existsByIdAndAtivoIsTrue(idAnuncio)) throw new AnuncioNaoExisteException(idAnuncio);
        return repository.findById(idAnuncio).get();
    }



}
