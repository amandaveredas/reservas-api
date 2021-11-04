package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.*;
import io.github.cwireset.tcc.exception.*;
import io.github.cwireset.tcc.repository.ReservaRepository;
import io.github.cwireset.tcc.request.CadastrarReservaRequest;
import io.github.cwireset.tcc.response.DadosAnuncioResponse;
import io.github.cwireset.tcc.response.DadosSolicitanteResponse;
import io.github.cwireset.tcc.response.InformacaoReservaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    ReservaRepository repository;
    @Autowired
    AnuncioService anuncioService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ImovelService imovelService;

    public InformacaoReservaResponse cadastrarReserva(CadastrarReservaRequest cadastrarReservaRequest) throws UsuarioIdNaoExisteException, AnuncioNaoExisteException, PeriodoInvalidoException, SolicitanteIgualAnuncianteException, NumeroMinimoPessoasException, NumeroMinimoDiariasException {

        LocalDateTime dataHoraInicialRequest = cadastrarReservaRequest.getPeriodo().getDataHoraInicial();
        LocalDateTime dataHoraFinalRequest = cadastrarReservaRequest.getPeriodo().getDataHoraFinal();
        Anuncio anuncio = anuncioService.buscarPeloId(cadastrarReservaRequest.getIdAnuncio());
        TipoImovel tipoImovel = anuncio.getImovel().getTipoImovel();
        Integer quantidadePessoas = cadastrarReservaRequest.getQuantidadePessoas();


        if (dataHoraInicialRequest.isAfter(dataHoraFinalRequest))
            throw new PeriodoInvalidoException("Período inválido! A data final da reserva precisa ser maior do que a data inicial.");

        LocalDate dataInicialRequest = LocalDate.of(dataHoraInicialRequest.getYear(),dataHoraInicialRequest.getMonthValue(),dataHoraInicialRequest.getDayOfMonth());
        LocalDate dataIFinalRequest = LocalDate.of(dataHoraFinalRequest.getYear(),dataHoraFinalRequest.getMonthValue(),dataHoraFinalRequest.getDayOfMonth());
        if(dataInicialRequest.isEqual(dataIFinalRequest))
            throw new PeriodoInvalidoException("Período inválido! O número mínimo de diárias precisa ser maior ou igual à 1.");

        Long idAnunciante = anuncio.getAnunciante().getId();
        if (cadastrarReservaRequest.getIdSolicitante() == idAnunciante)
            throw new SolicitanteIgualAnuncianteException();

        List<Reserva> reservasCoincidentes = repository.findAllByPeriodo_DataHoraInicialIsAfterOrPeriodo_DataHoraFinalIsBefore(dataHoraInicialRequest, dataHoraFinalRequest);
        for (Reserva r: reservasCoincidentes){
            if(r.getPeriodo().getDataHoraInicial().isBefore(dataHoraFinalRequest) &&
                r.getPeriodo().getDataHoraFinal().isAfter(dataHoraInicialRequest)){
                if (r.getPagamento().getStatus() == StatusPagamento.CANCELADO ||
                        r.getPagamento().getStatus() == StatusPagamento.ESTORNADO){
                    throw new PeriodoInvalidoException("Este anuncio já esta reservado para o período informado.");
                }
            }
            
        }

        if (tipoImovel.equals(TipoImovel.HOTEL) && quantidadePessoas <2)
            throw new NumeroMinimoPessoasException(quantidadePessoas, TipoImovel.HOTEL.getDescricao());

        long quantDiarias = ChronoUnit.DAYS.between(dataInicialRequest, dataIFinalRequest);
        if (tipoImovel.equals(TipoImovel.POUSADA) && quantDiarias<5)
            throw new NumeroMinimoDiariasException(quantDiarias, TipoImovel.POUSADA.getDescricao());

        Reserva reserva = new Reserva();

        //Dados do solicitante - response
        Usuario solicitante = usuarioService.buscarPeloId(cadastrarReservaRequest.getIdSolicitante());
        DadosSolicitanteResponse dadosSolicitanteResponse = new DadosSolicitanteResponse();
        dadosSolicitanteResponse.setId(cadastrarReservaRequest.getIdSolicitante());
        dadosSolicitanteResponse.setNome(solicitante.getNome());

        //Dados do anuncio - response
        DadosAnuncioResponse dadosAnuncioResponse = new DadosAnuncioResponse();
        dadosAnuncioResponse.setId(cadastrarReservaRequest.getIdAnuncio());
        dadosAnuncioResponse.setImovel(anuncio.getImovel());
        dadosAnuncioResponse.setAnunciante(anuncio.getAnunciante());
        dadosAnuncioResponse.setFormasAceitas(anuncio.getFormasAceitas());
        dadosAnuncioResponse.setDescricao(anuncio.getDescricao());

        //Período da reserva
        final int horaInicioReserva = 14;
        final int horaFimReserva = 12;
        LocalDateTime dataInicioReservaAjustada = ajustarHoraReserva(dataHoraInicialRequest,horaInicioReserva);
        LocalDateTime dataFimoReservaAjustada = ajustarHoraReserva(dataHoraFinalRequest,horaFimReserva);
        Periodo periodoAjustado = new Periodo();
        periodoAjustado.setDataHoraInicial(dataInicioReservaAjustada);
        periodoAjustado.setDataHoraFinal(dataFimoReservaAjustada);
        reserva.setPeriodo(periodoAjustado);

        //Pagamento da reserva
        Pagamento pagamento = new Pagamento();
        BigDecimal valorTotal = valorTotalReserva(dataInicialRequest, dataIFinalRequest, anuncio.getValorDiaria());
        pagamento.setValorTotal(valorTotal);
        reserva.setPagamento(pagamento);

        //Informações da reserva - response
        InformacaoReservaResponse informacaoReservaResponse = new InformacaoReservaResponse();
        informacaoReservaResponse.setIdReserva(reserva.getId());
        informacaoReservaResponse.setSolicitante(dadosSolicitanteResponse);
        informacaoReservaResponse.setQuantidadePessoas(quantidadePessoas);
        informacaoReservaResponse.setAnuncio(dadosAnuncioResponse);
        informacaoReservaResponse.setPeriodo(reserva.getPeriodo());
        informacaoReservaResponse.setPagamento(reserva.getPagamento());

        reserva.setDataHoraReserva(LocalDateTime.now());

        return informacaoReservaResponse;

    }

    private LocalDateTime ajustarHoraReserva(LocalDateTime dataOriginalReserva, int hora) {

        int diaOriginalReserva = dataOriginalReserva.getDayOfMonth();
        int mesOriginalReserva = dataOriginalReserva.getMonthValue();
        int anoOriginalReserva = dataOriginalReserva.getYear();
        final int minutosReserva = 0;

       LocalDateTime dataAjustadaReserva = LocalDateTime.of(anoOriginalReserva,
                                                           mesOriginalReserva,
                                                           diaOriginalReserva,
                                                            hora,
                                                            minutosReserva);

        return  dataAjustadaReserva;
    }



    private BigDecimal valorTotalReserva (LocalDate inicio, LocalDate fim, BigDecimal valorDiaria){
        long quantDiarias = ChronoUnit.DAYS.between(inicio, fim);
        return BigDecimal.valueOf(valorDiaria.doubleValue() * quantDiarias).setScale(2);
    }
}
