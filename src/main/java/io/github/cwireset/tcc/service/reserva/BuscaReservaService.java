package io.github.cwireset.tcc.service.reserva;

import io.github.cwireset.tcc.domain.Periodo;
import io.github.cwireset.tcc.domain.Reserva;
import io.github.cwireset.tcc.domain.Usuario;
import io.github.cwireset.tcc.exception.UsuarioIdNaoExisteException;
import io.github.cwireset.tcc.repository.ReservaRepository;
import io.github.cwireset.tcc.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BuscaReservaService {

    private UsuarioService usuarioService;
    private ReservaRepository repository;

    @Autowired
    public BuscaReservaService(UsuarioService usuarioService, ReservaRepository repository) {
        this.usuarioService = usuarioService;
        this.repository = repository;
    }

    public Page<Reserva> buscarReservasPorSolicitante(Long idSolicitante,
                                                      Periodo periodo,
                                                      Pageable pageable) {

        LocalDateTime inicioPeriodo = periodo.getDataHoraInicial();
        LocalDateTime fimPeriodo = periodo.getDataHoraFinal();

        try {
            Usuario solicitante = usuarioService.buscarPeloId(idSolicitante);
            if(inicioPeriodo == null && fimPeriodo == null){
                return repository.findAllBySolicitante(solicitante, pageable);
            }else if (inicioPeriodo == null){
                return repository.findAllBySolicitante(solicitante, pageable);
            }else if (fimPeriodo == null){
                return repository.findAllBySolicitante(solicitante, pageable);
            }else{
                return repository.findReservasBySolicitanteAndPeriodo_DataHoraInicialIsAfterAndPeriodo_DataHoraFinalIsBefore(solicitante,inicioPeriodo,fimPeriodo,pageable);
            }
        } catch (UsuarioIdNaoExisteException e) {
            return Page.empty(pageable);
        }
    }

    public Page<Reserva> buscarReservasPorAnunciante(Long idAnunciante, Pageable pageable) {
        try {
            Usuario anunciante = usuarioService.buscarPeloId(idAnunciante);
            return repository.findReservasByAnuncio_Anunciante(anunciante,pageable);
        } catch (UsuarioIdNaoExisteException e) {
            return Page.empty(pageable);
        }
    }


}
