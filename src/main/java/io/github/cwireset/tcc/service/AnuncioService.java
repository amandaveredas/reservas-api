package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.Anuncio;
import io.github.cwireset.tcc.domain.Imovel;
import io.github.cwireset.tcc.domain.Usuario;
import io.github.cwireset.tcc.exception.AnuncioNaoExisteException;
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

    private AnuncioRepository repository;
    private ImovelService imovelService;
    private UsuarioService usuarioService;

    @Autowired
    public AnuncioService(AnuncioRepository repository, ImovelService imovelService, UsuarioService usuarioService) {
        this.repository = repository;
        this.imovelService = imovelService;
        this.usuarioService = usuarioService;
    }

    public Anuncio cadastrar(CadastrarAnuncioRequest cadastrarAnuncioRequest) throws ImovelIdNaoExisteException, UsuarioIdNaoExisteException, ImovelAmbiguidadeAnunciosException {

        Long idImovel = cadastrarAnuncioRequest.getIdImovel();
        Imovel imovel = imovelService.buscarPeloId(idImovel);


        if(repository.existsByImovel(imovel)) throw new ImovelAmbiguidadeAnunciosException(idImovel);

        Usuario anunciante = usuarioService.buscarPeloId(cadastrarAnuncioRequest.getIdAnunciante());

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
           return null;
        }
    }

    public void excluirLogicamente(Long idAnuncio) throws AnuncioNaoExisteException {
        if(!repository.existsById(idAnuncio)) throw new AnuncioNaoExisteException(idAnuncio);
        Anuncio anuncio = repository.findById(idAnuncio).get();
        if (anuncio.isAtivo() == false) throw new AnuncioNaoExisteException(idAnuncio);

        anuncio.setAtivo(false);
        repository.save(anuncio);


    }

    public Anuncio buscarPeloId(Long idAnuncio) throws AnuncioNaoExisteException {
        if(!repository.existsById(idAnuncio)) throw new AnuncioNaoExisteException(idAnuncio);
        return repository.findById(idAnuncio).get();
    }


}
