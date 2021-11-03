package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.Anuncio;
import io.github.cwireset.tcc.domain.Imovel;
import io.github.cwireset.tcc.domain.Usuario;
import io.github.cwireset.tcc.exception.ImovelIdNaoExisteException;
import io.github.cwireset.tcc.exception.ImovelAmbiguidadeAnunciosException;
import io.github.cwireset.tcc.exception.UsuarioIdNaoExisteException;
import io.github.cwireset.tcc.repository.AnuncioRepository;
import io.github.cwireset.tcc.request.CadastrarAnuncioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AnuncioService {

    @Autowired
    AnuncioRepository repository;
    @Autowired
    ImovelService imovelService;
    @Autowired
    UsuarioService usuarioService;


    public Anuncio cadastrar(CadastrarAnuncioRequest cadastrarAnuncioRequest) throws ImovelIdNaoExisteException, UsuarioIdNaoExisteException, ImovelAmbiguidadeAnunciosException {

        Long idImovel = cadastrarAnuncioRequest.getIdImovel();
        Imovel imovel = imovelService.buscarPeloId(idImovel);
        if(repository.existsByImovel(imovel)) throw new ImovelAmbiguidadeAnunciosException(idImovel);

        Usuario anunciante = usuarioService.buscarPeloId(cadastrarAnuncioRequest.getIdAnunciante());

        Anuncio anuncio = new Anuncio();
        anuncio.setTipoAnuncio(cadastrarAnuncioRequest.getTipoAnuncio());
        anuncio.setImovel(imovel);
        anuncio.setAnunciante(anunciante);
        anuncio.setValorDiaria(cadastrarAnuncioRequest.getValorDiaria());
        anuncio.setFormasAceitas(cadastrarAnuncioRequest.getFormasAceitas());
        anuncio.setDescricao(cadastrarAnuncioRequest.getDescricao());

        return repository.save(anuncio);
    }

    public boolean verificaSeExisteAnuncioPorImovel(Imovel imovel){
        return (repository.existsByImovel(imovel));
    }

    public Page<Anuncio> listarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
