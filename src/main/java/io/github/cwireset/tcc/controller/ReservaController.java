package io.github.cwireset.tcc.controller;

import io.github.cwireset.tcc.exception.*;
import io.github.cwireset.tcc.request.CadastrarReservaRequest;
import io.github.cwireset.tcc.response.InformacaoReservaResponse;
import io.github.cwireset.tcc.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
}
