package io.github.cwireset.tcc.controller;

import io.github.cwireset.tcc.domain.FormaPagamento;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/reservas")
public class ReservaController {


    private ReservaService reservaService;

    @Autowired
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InformacaoReservaResponse realizarReserva(@RequestBody @Valid CadastrarReservaRequest cadastrarReservaRequest) throws AnuncioNaoExisteException, UsuarioIdNaoExisteException, SolicitanteIgualAnuncianteException, NumeroMinimoPessoasException, PeriodoInvalidoException, NumeroMinimoDiariasException {
        return reservaService.cadastrarReserva(cadastrarReservaRequest);
    }

    @GetMapping("/solicitantes/{idSolicitante}")
    public Page<Reserva> listarPorSolicitante( @PathVariable Long idSolicitante,
                                              Periodo periodo,
                                               @PageableDefault(sort = "periodo_dataHoraFinal",
            direction = Sort.Direction.DESC) @ApiIgnore Pageable pageable ){

        return reservaService.buscarReservasPorSolicitante(idSolicitante, periodo, pageable);
    }


    @GetMapping("/anuncios/anunciantes/{idAnunciante}")
    public Page<Reserva> listarPorAnunciante( @PathVariable Long idAnunciante,
                                               @PageableDefault(sort = "periodo_dataHoraFinal",
                                                       direction = Sort.Direction.DESC) @ApiIgnore Pageable pageable ){


        return reservaService.buscarReservasPorAnunciante(idAnunciante, pageable);
    }

    @PutMapping("/{idReserva}/pagamentos")
    public void pagarReserva(@PathVariable Long idReserva, @RequestBody String formaPagamentoRequest) throws FormaPagaMentoInvalidaException, ReservaNaoPendenteException, ReservaNaoExisteException {
        Enum<FormaPagamento> formaPagamento = FormaPagamento.valueOf(formaPagamentoRequest.substring(1,formaPagamentoRequest.length()-1));
        reservaService.pagar(idReserva, formaPagamento);
    }

    @PutMapping("/{idReserva}/pagamentos/cancelar")
    public void cancelarReserva(@PathVariable Long idReserva) throws ReservaNaoPendenteException, ReservaNaoExisteException {
        reservaService.cancelar(idReserva);
    }

    @PutMapping("/{idReserva}/pagamentos/estornar")
    public void estornarReserva(@PathVariable Long idReserva) throws ReservaNaoExisteException, ReservaNaoPagaException {
        reservaService.estornar(idReserva);
    }





}
