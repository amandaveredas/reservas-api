package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.Imovel;
import io.github.cwireset.tcc.domain.Usuario;
import io.github.cwireset.tcc.exception.UsuarioIdNaoExisteException;
import io.github.cwireset.tcc.repository.ImovelRepository;
import io.github.cwireset.tcc.request.CadastrarImovelRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ImovelService {

    @Autowired
    ImovelRepository repository;
    @Autowired
    UsuarioService usuarioService;

    public Imovel salvar(CadastrarImovelRequest cadastrarImovelRequest) throws UsuarioIdNaoExisteException {

        Usuario proprietario = usuarioService.buscarPeloId(cadastrarImovelRequest.getIdProprietario());

        Imovel imovel = new Imovel();
        imovel.setIdentificacao(cadastrarImovelRequest.getIdentificacao());
        imovel.setTipoImovel(cadastrarImovelRequest.getTipoImovel());
        imovel.setEndereco(cadastrarImovelRequest.getEndereco());
        imovel.setProprietario(proprietario);
        imovel.setCaracteristicas(cadastrarImovelRequest.getCaracteristicas());

        return repository.save(imovel);
    }

    public Page<Imovel> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Imovel> buscarImoveisPorProprietario(Pageable pageable, Long idProprietario) {
        Usuario proprietario = null;
        try {
            proprietario = usuarioService.buscarPeloId(idProprietario);
            return repository.findAllByProprietario(pageable, proprietario);
        } catch (UsuarioIdNaoExisteException e) {
            return null;
        }
    }
}
