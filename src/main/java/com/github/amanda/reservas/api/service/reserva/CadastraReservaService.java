package com.github.amanda.reservas.api.service.reserva;

import com.github.amanda.reservas.api.domain.*;
import com.github.amanda.reservas.api.exception.*;
import com.github.amanda.reservas.api.repository.ReservaRepository;
import com.github.amanda.reservas.api.request.CadastrarReservaRequest;
import com.github.amanda.reservas.api.response.DadosAnuncioResponse;
import com.github.amanda.reservas.api.response.DadosSolicitanteResponse;
import com.github.amanda.reservas.api.response.InformacaoReservaResponse;
import com.github.amanda.reservas.api.service.AnuncioService;
import com.github.amanda.reservas.api.service.ImovelService;
import com.github.amanda.reservas.api.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CadastraReservaService {

    private ReservaRepository repository;
    private AnuncioService anuncioService;
    private UsuarioService usuarioService;
    private ImovelService imovelService;

    @Autowired
    public CadastraReservaService(ReservaRepository repository, AnuncioService anuncioService, UsuarioService usuarioService, ImovelService imovelService) {
        this.repository = repository;
        this.anuncioService = anuncioService;
        this.usuarioService = usuarioService;
        this.imovelService = imovelService;
    }

    public InformacaoReservaResponse cadastrarReserva(CadastrarReservaRequest cadastrarReservaRequest) throws UsuarioIdNaoExisteException, AnuncioNaoExisteException, PeriodoInvalidoException, SolicitanteIgualAnuncianteException, NumeroMinimoPessoasException, NumeroMinimoDiariasException {

        Usuario solicitante = usuarioService.buscarPeloId(cadastrarReservaRequest.getIdSolicitante());
        Anuncio anuncio = anuncioService.buscarPeloId(cadastrarReservaRequest.getIdAnuncio());
        LocalDateTime dataHoraInicialRequest = cadastrarReservaRequest.getPeriodo().getDataHoraInicial();
        LocalDateTime dataHoraFinalRequest = cadastrarReservaRequest.getPeriodo().getDataHoraFinal();
        TipoImovel tipoImovel = anuncio.getImovel().getTipoImovel();
        Integer quantidadePessoas = cadastrarReservaRequest.getQuantidadePessoas();


        if (dataHoraInicialRequest.isAfter(dataHoraFinalRequest))
            throw new PeriodoInvalidoException("Período inválido! A data final da reserva precisa ser maior do que a data inicial.");

        LocalDate dataInicialRequest = LocalDate.of(dataHoraInicialRequest.getYear(),dataHoraInicialRequest.getMonthValue(),dataHoraInicialRequest.getDayOfMonth());
        LocalDate dataIFinalRequest = LocalDate.of(dataHoraFinalRequest.getYear(),dataHoraFinalRequest.getMonthValue(),dataHoraFinalRequest.getDayOfMonth());

        if(dataInicialRequest.isEqual(dataIFinalRequest))
            throw new PeriodoInvalidoException("Período inválido! O número mínimo de diárias precisa ser maior ou igual à 1.");

        if (solicitante.getId() == anuncio.getAnunciante().getId())
            throw new SolicitanteIgualAnuncianteException();

        if (tipoImovel.equals(TipoImovel.HOTEL) && quantidadePessoas <2)
            throw new NumeroMinimoPessoasException(2, TipoImovel.HOTEL.getDescricao());

        long quantDiarias = ChronoUnit.DAYS.between(dataInicialRequest, dataIFinalRequest);
        if (tipoImovel.equals(TipoImovel.POUSADA) && quantDiarias<5)
            throw new NumeroMinimoDiariasException(5, TipoImovel.POUSADA.getDescricao());

        final int horaInicioReserva = 14;
        final int horaFimReserva = 12;
        Periodo periodoAjustado = ajustaPeriodo(dataHoraInicialRequest, dataHoraFinalRequest, horaInicioReserva, horaFimReserva);

        Reserva reservaRequestHoraAjustada = Reserva.builder()
                .periodo(periodoAjustado)
                .anuncio(anuncio)
                .build();

        List<Reserva> reservasNoPeriodo = repository.findAllByPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsGreaterThanAndPeriodo_DataHoraFinalIsLessThan(dataHoraInicialRequest, dataHoraInicialRequest,dataHoraFinalRequest,dataHoraFinalRequest, dataHoraInicialRequest,dataHoraFinalRequest);
        if(verificaSeExisteReservasNoMesmoPeriodo(reservasNoPeriodo, reservaRequestHoraAjustada))
            throw new PeriodoInvalidoException("Este anuncio já esta reservado para o período informado.");

        Pagamento pagamento = dadosPagamento(anuncio, dataInicialRequest, dataIFinalRequest);
        LocalDateTime dataRegistroFormatada = formataData();

        Reserva reserva = Reserva.builder()
                .solicitante(solicitante)
                .anuncio(anuncio)
                .periodo(periodoAjustado)
                .quantidadePessoas(quantidadePessoas)
                .dataHoraReserva(dataRegistroFormatada)
                .pagamento(pagamento)
                .build();

        repository.save(reserva);

        return criaResponseDaReserva(reserva);

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

    private boolean verificaSeExisteReservasNoMesmoPeriodo(List<Reserva> reservasNoPeriodo, Reserva reservaRequestHoraAjustada){
        LocalDateTime dataHoraInicialRequest = reservaRequestHoraAjustada.getPeriodo().getDataHoraInicial();
        LocalDateTime dataHoraFinalRequest = reservaRequestHoraAjustada.getPeriodo().getDataHoraFinal();

        for (Reserva r: reservasNoPeriodo){
            if (r.getAnuncio().getId() == reservaRequestHoraAjustada.getAnuncio().getId()){
                if(r.getPeriodo().getDataHoraInicial().isBefore(dataHoraFinalRequest) &&
                        r.getPeriodo().getDataHoraFinal().isAfter(dataHoraInicialRequest)){
                    if (r.getPagamento().getStatus() == StatusPagamento.PENDENTE ||
                            r.getPagamento().getStatus() == StatusPagamento.PAGO){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private InformacaoReservaResponse criaResponseDaReserva (Reserva reserva){

        DadosSolicitanteResponse dadosSolicitanteResponse = criaDadosSolicitanteResponse(reserva);

        DadosAnuncioResponse dadosAnuncioResponse = criaDadosAnuncioResponse(reserva);

        InformacaoReservaResponse informacaoReservaResponse = InformacaoReservaResponse.builder()
                .idReserva(reserva.getId())
                .solicitante(dadosSolicitanteResponse)
                .quantidadePessoas(reserva.getQuantidadePessoas())
                .anuncio(dadosAnuncioResponse)
                .periodo(reserva.getPeriodo())
                .pagamento(reserva.getPagamento())
                .build();

        return informacaoReservaResponse;

    }

    private DadosAnuncioResponse criaDadosAnuncioResponse(Reserva reserva) {
        DadosAnuncioResponse dadosAnuncioResponse = DadosAnuncioResponse.builder()
                .id(reserva.getAnuncio().getId())
                .imovel(reserva.getAnuncio().getImovel())
                .anunciante(reserva.getAnuncio().getAnunciante())
                .formasAceitas(reserva.getAnuncio().getFormasAceitas())
                .descricao(reserva.getAnuncio().getDescricao())
                .build();
        return dadosAnuncioResponse;
    }

    private DadosSolicitanteResponse criaDadosSolicitanteResponse(Reserva reserva) {
        DadosSolicitanteResponse dadosSolicitanteResponse = DadosSolicitanteResponse.builder()
                .id(reserva.getSolicitante().getId())
                .nome(reserva.getSolicitante().getNome())
                .build();
        return dadosSolicitanteResponse;
    }

    private Periodo ajustaPeriodo(LocalDateTime dataHoraInicialRequest, LocalDateTime dataHoraFinalRequest, int horaInicioReserva, int horaFimReserva) {
        LocalDateTime dataInicioReservaAjustada = ajustarHoraReserva(dataHoraInicialRequest, horaInicioReserva);
        LocalDateTime dataFimoReservaAjustada = ajustarHoraReserva(dataHoraFinalRequest, horaFimReserva);
        Periodo periodoAjustado = new Periodo();
        periodoAjustado.setDataHoraInicial(dataInicioReservaAjustada);
        periodoAjustado.setDataHoraFinal(dataFimoReservaAjustada);
        return periodoAjustado;
    }

    private Pagamento dadosPagamento(Anuncio anuncio, LocalDate dataInicialRequest, LocalDate dataIFinalRequest) {
        BigDecimal valorTotal = valorTotalReserva(dataInicialRequest, dataIFinalRequest, anuncio.getValorDiaria());
        Pagamento pagamento = Pagamento.builder()
                .valorTotal(valorTotal)
                .status(StatusPagamento.PENDENTE)
                .build();
        return pagamento;
    }

    private LocalDateTime formataData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dataRegistroFormatada = LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);
        return dataRegistroFormatada;
    }
}
