package io.github.cwireset.tcc.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.cwireset.tcc.domain.Imovel;
import io.github.cwireset.tcc.domain.Periodo;
import io.github.cwireset.tcc.domain.Reserva;
import io.github.cwireset.tcc.exception.*;
import io.github.cwireset.tcc.request.CadastrarReservaRequest;
import io.github.cwireset.tcc.response.InformacaoReservaResponse;
import io.github.cwireset.tcc.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    ReservaService reservaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InformacaoReservaResponse realizarReserva(@RequestBody @Valid CadastrarReservaRequest cadastrarReservaRequest) throws AnuncioNaoExisteException, UsuarioIdNaoExisteException, SolicitanteIgualAnuncianteException, NumeroMinimoPessoasException, PeriodoInvalidoException, NumeroMinimoDiariasException {
        return reservaService.cadastrarReserva(cadastrarReservaRequest);
    }

    @GetMapping("/solicitantes/{idSolicitante}")
    public Page<Reserva> listarPorSolicitante( @PathVariable Long idSolicitante,
                                               @RequestParam (required = false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime dataHoraInicial,
                                               @RequestParam (required = false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime dataHoraFinal,
                                               @PageableDefault(sort = "periodo_dataHoraFinal",
            direction = Sort.Direction.DESC,
            page = 0,
            size = 10) Pageable pageable ){


        return reservaService.buscarReservasPorSolicitante(idSolicitante, dataHoraInicial,dataHoraFinal, pageable);
    }





}
