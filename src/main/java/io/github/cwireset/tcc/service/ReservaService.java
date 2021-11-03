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
        Usuario solicitante = usuarioService.buscarPeloId(cadastrarReservaRequest.getIdSolicitante());


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
            if (r.getAnuncio().getId() == cadastrarReservaRequest.getIdAnuncio()){
                if(r.getPeriodo().getDataHoraInicial().isBefore(dataHoraFinalRequest) &&
                        r.getPeriodo().getDataHoraFinal().isAfter(dataHoraInicialRequest)){
                    if (r.getPagamento().getStatus() == StatusPagamento.PENDENTE ||
                            r.getPagamento().getStatus() == StatusPagamento.PAGO){
                        throw new PeriodoInvalidoException("Este anuncio já esta reservado para o período informado.");
                    }
                }
            }


        }

        if (tipoImovel.equals(TipoImovel.HOTEL) && quantidadePessoas <2)
            throw new NumeroMinimoPessoasException(2, TipoImovel.HOTEL.getDescricao());

        long quantDiarias = ChronoUnit.DAYS.between(dataInicialRequest, dataIFinalRequest);
        if (tipoImovel.equals(TipoImovel.POUSADA) && quantDiarias<5)
            throw new NumeroMinimoDiariasException(5, TipoImovel.POUSADA.getDescricao());

        Reserva reserva = new Reserva();

        //Período da reserva
        final int horaInicioReserva = 14;
        final int horaFimReserva = 12;
        LocalDateTime dataInicioReservaAjustada = ajustarHoraReserva(dataHoraInicialRequest,horaInicioReserva);
        LocalDateTime dataFimoReservaAjustada = ajustarHoraReserva(dataHoraFinalRequest,horaFimReserva);
        Periodo periodoAjustado = new Periodo();
        periodoAjustado.setDataHoraInicial(dataInicioReservaAjustada);
        periodoAjustado.setDataHoraFinal(dataFimoReservaAjustada);

        //Pagamento da reserva
        Pagamento pagamento = new Pagamento();
        BigDecimal valorTotal = valorTotalReserva(dataInicialRequest, dataIFinalRequest, anuncio.getValorDiaria());
        pagamento.setValorTotal(valorTotal);
        pagamento.setStatus(StatusPagamento.PENDENTE);

        //setando e salvando dados da reserva
        reserva.setSolicitante(solicitante);
        reserva.setAnuncio(anuncio);
        reserva.setPeriodo(periodoAjustado);
        reserva.setQuantidadePessoas(quantidadePessoas);
        reserva.setDataHoraReserva(LocalDateTime.now());
        reserva.setPagamento(pagamento);
        repository.save(reserva);

        //Dados do solicitante - response
        DadosSolicitanteResponse dadosSolicitanteResponse = new DadosSolicitanteResponse();
        dadosSolicitanteResponse.setId(reserva.getSolicitante().getId());
        dadosSolicitanteResponse.setNome(reserva.getSolicitante().getNome());

        //Dados do anuncio - response
        DadosAnuncioResponse dadosAnuncioResponse = new DadosAnuncioResponse();
        dadosAnuncioResponse.setId(reserva.getAnuncio().getId());
        dadosAnuncioResponse.setImovel(reserva.getAnuncio().getImovel());
        dadosAnuncioResponse.setAnunciante(reserva.getAnuncio().getAnunciante());
        dadosAnuncioResponse.setFormasAceitas(reserva.getAnuncio().getFormasAceitas());
        dadosAnuncioResponse.setDescricao(reserva.getAnuncio().getDescricao());

        //Informações da reserva - response
        InformacaoReservaResponse informacaoReservaResponse = new InformacaoReservaResponse();
        informacaoReservaResponse.setIdReserva(reserva.getId());
        informacaoReservaResponse.setSolicitante(dadosSolicitanteResponse);
        informacaoReservaResponse.setQuantidadePessoas(reserva.getQuantidadePessoas());
        informacaoReservaResponse.setAnuncio(dadosAnuncioResponse);
        informacaoReservaResponse.setPeriodo(reserva.getPeriodo());
        informacaoReservaResponse.setPagamento(reserva.getPagamento());

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