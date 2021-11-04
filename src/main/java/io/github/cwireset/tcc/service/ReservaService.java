package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.*;
import io.github.cwireset.tcc.exception.*;
import io.github.cwireset.tcc.repository.ReservaRepository;
import io.github.cwireset.tcc.request.CadastrarReservaRequest;
import io.github.cwireset.tcc.response.DadosAnuncioResponse;
import io.github.cwireset.tcc.response.DadosSolicitanteResponse;
import io.github.cwireset.tcc.response.InformacaoReservaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.AssertTrue;
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

    public Page<Reserva> buscarReservasPorSolicitante(Long idSolicitante,
                                                      LocalDateTime inicioPeriodo,
                                                      LocalDateTime fimPeriodo,
                                                      Pageable pageable) {

        try {
            Usuario solicitante = usuarioService.buscarPeloId(idSolicitante);
            if(inicioPeriodo == null && fimPeriodo == null){
                return repository.findAllBySolicitante(solicitante, pageable);
            }else if (inicioPeriodo == null){
                return repository.findReservasBySolicitanteAndPeriodo_DataHoraFinalIsBefore(solicitante, fimPeriodo,pageable);
            }else if (fimPeriodo == null){
                return repository.findReservasBySolicitanteAndPeriodo_DataHoraInicialIsAfter(solicitante, inicioPeriodo, pageable);
            }else{
                return repository.findReservasBySolicitanteAndPeriodo_DataHoraInicialIsAfterAndPeriodo_DataHoraFinalIsBefore(solicitante,inicioPeriodo,fimPeriodo,pageable);
            }
        } catch (UsuarioIdNaoExisteException e) {
            return null;
        }
    }

    public Page<Reserva> buscarReservasPorAnunciante(Long idAnunciante, Pageable pageable) {
        try {
            Usuario anunciante = usuarioService.buscarPeloId(idAnunciante);
            return repository.findReservasByAnuncio_Anunciante(anunciante,pageable);
        } catch (UsuarioIdNaoExisteException e) {
            return null;
        }
    }

    public void pagar(Long idReserva, Enum<FormaPagamento> formaPagamento) throws ReservaNaoExisteException, FormaPagaMentoInvalidaException, ReservaNaoPendenteException {
        Reserva reserva = verificaSeExisteERetornaAReserva(idReserva);
        List<FormaPagamento> formasAceitas = reserva.getAnuncio().getFormasAceitas();

        if (!formasAceitas.contains(formaPagamento)){
            String aceitas = "";
            for(FormaPagamento f: formasAceitas){
                aceitas+= f+", ";
            }
            aceitas = aceitas.substring(0,aceitas.length()-2);
            throw new FormaPagaMentoInvalidaException(formaPagamento,aceitas);
        }

        if(!verificaSeReservaPendente(reserva)){
            throw new ReservaNaoPendenteException( "Não é possível realizar o pagamento para esta reserva, pois ela não está no status PENDENTE.");
        };

        reserva.getPagamento().setStatus(StatusPagamento.PAGO);
        repository.save(reserva);

    }



    public void cancelar(Long idReserva) throws ReservaNaoExisteException, ReservaNaoPendenteException {
        Reserva reserva = verificaSeExisteERetornaAReserva(idReserva);

        if(!verificaSeReservaPendente(reserva)){
            throw new ReservaNaoPendenteException("Não é possível realizar o cancelamento para esta reserva, pois ela não está no status PENDENTE.");
        };

        reserva.getPagamento().setStatus(StatusPagamento.CANCELADO);
        repository.save(reserva);
    }

    public void estornar(Long idReserva) throws ReservaNaoExisteException, ReservaNaoPagaException {
        Reserva reserva = verificaSeExisteERetornaAReserva(idReserva);
        if(!verificaSeReservaPaga(reserva)){
            throw new ReservaNaoPagaException();
        };
        reserva.getPagamento().setStatus(StatusPagamento.ESTORNADO);
        repository.save(reserva);
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

    private Reserva verificaSeExisteERetornaAReserva(Long idReserva) throws ReservaNaoExisteException {
        if(!repository.existsById(idReserva)) throw new ReservaNaoExisteException(idReserva);

        Reserva reserva = repository.findById(idReserva).get();
        return reserva;
    }

    private boolean verificaSeReservaPendente(Reserva reserva) throws ReservaNaoPendenteException {
        StatusPagamento status = reserva.getPagamento().getStatus();
        if(!status.equals(StatusPagamento.PENDENTE)){
            return false;
        }
        return true;
    }

    private boolean verificaSeReservaPaga(Reserva reserva) {
        StatusPagamento status = reserva.getPagamento().getStatus();
        if(!status.equals(StatusPagamento.PAGO)){
            return false;
        }
        return true;
    }


}
