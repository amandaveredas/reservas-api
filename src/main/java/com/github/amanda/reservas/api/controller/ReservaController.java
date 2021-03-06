package com.github.amanda.reservas.api.controller;

import com.github.amanda.reservas.api.domain.FormaPagamento;
import com.github.amanda.reservas.api.domain.Periodo;
import com.github.amanda.reservas.api.domain.Reserva;
import com.github.amanda.reservas.api.exception.*;
import com.github.amanda.reservas.api.request.CadastrarReservaRequest;
import com.github.amanda.reservas.api.response.InformacaoReservaResponse;
import com.github.amanda.reservas.api.service.reserva.BuscaReservaService;
import com.github.amanda.reservas.api.service.reserva.CadastraReservaService;
import com.github.amanda.reservas.api.service.reserva.PagamentoReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/reservas")
public class ReservaController {


    private CadastraReservaService cadastraReservaService;
    private PagamentoReservaService pagamentoReservaService;
    private BuscaReservaService buscaReservaService;

    @Autowired
    public ReservaController(CadastraReservaService cadastraReservaService, PagamentoReservaService pagamentoReservaService, BuscaReservaService buscaReservaService) {
        this.cadastraReservaService = cadastraReservaService;
        this.pagamentoReservaService = pagamentoReservaService;
        this.buscaReservaService = buscaReservaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InformacaoReservaResponse realizarReserva(@RequestBody @Valid CadastrarReservaRequest cadastrarReservaRequest) throws AnuncioNaoExisteException, UsuarioIdNaoExisteException, SolicitanteIgualAnuncianteException, NumeroMinimoPessoasException, PeriodoInvalidoException, NumeroMinimoDiariasException {
        return cadastraReservaService.cadastrarReserva(cadastrarReservaRequest);
    }

    @GetMapping("/solicitantes/{idSolicitante}")
    public Page<Reserva> listarPorSolicitante( @PathVariable Long idSolicitante,
                                              Periodo periodo,
                                               @PageableDefault(sort = "periodo_dataHoraFinal",
            direction = Sort.Direction.DESC) @ApiIgnore Pageable pageable ){

        return buscaReservaService.buscarReservasPorSolicitante(idSolicitante, periodo, pageable);
    }


    @GetMapping("/anuncios/anunciantes/{idAnunciante}")
    public Page<Reserva> listarPorAnunciante( @PathVariable Long idAnunciante,
                                               @PageableDefault(sort = "periodo_dataHoraFinal",
                                                       direction = Sort.Direction.DESC) @ApiIgnore Pageable pageable ){


        return buscaReservaService.buscarReservasPorAnunciante(idAnunciante, pageable);
    }

    @PutMapping("/{idReserva}/pagamentos")
    public void pagarReserva(@PathVariable Long idReserva, @RequestBody String formaPagamentoRequest) throws FormaPagaMentoInvalidaException, ReservaNaoPendenteException, ReservaNaoExisteException {
        Enum<FormaPagamento> formaPagamento = FormaPagamento.valueOf(formaPagamentoRequest.substring(1,formaPagamentoRequest.length()-1));
        pagamentoReservaService.pagar(idReserva, formaPagamento);
    }

    @PutMapping("/{idReserva}/pagamentos/cancelar")
    public void cancelarReserva(@PathVariable Long idReserva) throws ReservaNaoPendenteException, ReservaNaoExisteException {
        pagamentoReservaService.cancelar(idReserva);
    }

    @PutMapping("/{idReserva}/pagamentos/estornar")
    public void estornarReserva(@PathVariable Long idReserva) throws ReservaNaoExisteException, ReservaNaoPagaException {
        pagamentoReservaService.estornar(idReserva);
    }





}
