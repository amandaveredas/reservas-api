package io.github.cwireset.tcc.service.reserva;

import io.github.cwireset.tcc.domain.*;
import io.github.cwireset.tcc.exception.*;
import io.github.cwireset.tcc.repository.ReservaRepository;
import io.github.cwireset.tcc.request.CadastrarReservaRequest;
import io.github.cwireset.tcc.response.InformacaoReservaResponse;
import io.github.cwireset.tcc.service.AnuncioService;
import io.github.cwireset.tcc.service.ImovelService;
import io.github.cwireset.tcc.service.UsuarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CadastraReservaServiceTest {

    @InjectMocks
    CadastraReservaService service;

    @Mock
    private ReservaRepository repository;
    @Mock
    private AnuncioService anuncioService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private ImovelService imovelService;

    @BeforeEach
    void setUp(){

    }

    private InformacaoReservaResponse buildResponse(){
        return null;
    }

    private CadastrarReservaRequest buildRequest(){

        Periodo periodo = new Periodo();
        periodo.setDataHoraFinal(LocalDateTime.of(2021,11,10,10,00,00));
        periodo.setDataHoraInicial(LocalDateTime.of(2021,11,05,18,00,00));

        CadastrarReservaRequest cadastrarReservaRequest = CadastrarReservaRequest.builder()
                .idAnuncio(1L)
                .idSolicitante(1L)
                .periodo(periodo)
                .quantidadePessoas(2).build();

        return cadastrarReservaRequest;
    }

    @Test
    public void deveRetornarErroUsuarioSolicitanteNaoEncontrado() throws UsuarioIdNaoExisteException {
        CadastrarReservaRequest request = buildRequest();
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        when(usuarioService.buscarPeloId(idCaptor.capture())).thenThrow(UsuarioIdNaoExisteException.class);

        assertThrows(UsuarioIdNaoExisteException.class, ()-> service.cadastrarReserva(request));
        Assertions.assertEquals(request.getIdSolicitante(), idCaptor.getValue());


    }

    @Test
    public void deveRetornarErroAnuncioNaoEncontrado() throws UsuarioIdNaoExisteException, AnuncioNaoExisteException {
        CadastrarReservaRequest request = buildRequest();
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        when(usuarioService.buscarPeloId(request.getIdSolicitante())).thenReturn(Usuario.builder().build());
        when(anuncioService.buscarPeloId(idCaptor.capture())).thenThrow(AnuncioNaoExisteException.class);

        assertThrows(AnuncioNaoExisteException.class, ()-> service.cadastrarReserva(request));
        Assertions.assertEquals(request.getIdSolicitante(), idCaptor.getValue());

    }

    @Test
    public void deveRetornarErroAtributosObrigatoriosFaltantes(){
        CadastrarReservaRequest request = buildRequest();
        request.setPeriodo(null);
        request.setIdAnuncio(null);
        request.setIdSolicitante(null);
        request.setQuantidadePessoas(null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<CadastrarReservaRequest>> violations = validator.validate(request);

        assertEquals(4, violations.size());
    }

    @Test
    public void deveRetornarErroDataInicialMaiorQueDataFinal() throws AnuncioNaoExisteException {
        CadastrarReservaRequest request = buildRequest();
        request.getPeriodo().setDataHoraFinal(LocalDateTime.of(2021,11,10,10,00,00));
        request.getPeriodo().setDataHoraInicial(LocalDateTime.of(2021,11,11,10,00,00));
        String msg = "Período inválido! A data final da reserva precisa ser maior do que a data inicial.";

        when(anuncioService.buscarPeloId(1L)).thenReturn(Anuncio.builder().imovel(Imovel.builder().build()).build());

        PeriodoInvalidoException e = assertThrows(PeriodoInvalidoException.class, ()->service.cadastrarReserva(request));
        assertEquals(msg, e.getMessage());
    }

    @Test
    public void deveRetornarErroPeriodoMenorUmDia() throws AnuncioNaoExisteException {
        CadastrarReservaRequest request = buildRequest();
        request.getPeriodo().setDataHoraFinal(LocalDateTime.of(2021,11,10,10,00,00));
        request.getPeriodo().setDataHoraInicial(LocalDateTime.of(2021,11,10,8,00,00));
        String msg = "Período inválido! O número mínimo de diárias precisa ser maior ou igual à 1.";

        when(anuncioService.buscarPeloId(1L)).thenReturn(Anuncio.builder().imovel(Imovel.builder().build()).build());

        PeriodoInvalidoException e = assertThrows(PeriodoInvalidoException.class, ()->service.cadastrarReserva(request));
        assertEquals(msg, e.getMessage());
    }

    @Test
    public void deveRetornarErroSolicitanteMesmoQueAnunciante() throws AnuncioNaoExisteException, UsuarioIdNaoExisteException {
        CadastrarReservaRequest request = buildRequest();
        ArgumentCaptor<Long> idAnuncioCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> idSolicitanteCaptor = ArgumentCaptor.forClass(Long.class);
        String msg = "O solicitante de uma reserva não pode ser o próprio anunciante.";

        when(usuarioService.buscarPeloId(idSolicitanteCaptor.capture())).thenReturn(Usuario.builder()
                .id(request.getIdSolicitante()).build());
        when(anuncioService.buscarPeloId(idAnuncioCaptor.capture()))
                .thenReturn(Anuncio.builder()
                        .anunciante(Usuario.builder()
                                .id(request.getIdSolicitante()).build())
                        .imovel(Imovel.builder().build()).build());

        SolicitanteIgualAnuncianteException e = assertThrows(SolicitanteIgualAnuncianteException.class, ()->service.cadastrarReserva(request));
        assertEquals(msg, e.getMessage());
        assertEquals(request.getIdAnuncio(),idAnuncioCaptor.getValue());
        assertEquals(request.getIdSolicitante(),idSolicitanteCaptor.getValue());
    }

    @Test
    public void deveRetornarErroNumeroDePessoasMenorQue2ParaHotel() throws UsuarioIdNaoExisteException, AnuncioNaoExisteException {
        CadastrarReservaRequest request = buildRequest();
        request.setQuantidadePessoas(1);
        String msg = "Não é possivel realizar uma reserva com menos de 2 pessoas para imóveis do tipo Hotel";

        when(usuarioService.buscarPeloId(request.getIdSolicitante())).thenReturn(Usuario.builder()
                .id(2L).build());
        when(anuncioService.buscarPeloId(request.getIdAnuncio()))
                .thenReturn(Anuncio.builder()
                        .anunciante(Usuario.builder().build())
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.HOTEL).build()).build());

        NumeroMinimoPessoasException e = assertThrows(NumeroMinimoPessoasException.class, ()->service.cadastrarReserva(request));
        assertEquals(msg, e.getMessage());
    }

    @Test
    public void deveRetornarErroNumeroDeDiariasMenorQue5ParaPousada() throws UsuarioIdNaoExisteException, AnuncioNaoExisteException {
        CadastrarReservaRequest request = buildRequest();
        request.getPeriodo().setDataHoraInicial(LocalDateTime.of(2021,11,6,10,00,00));
        String msg = "Não é possivel realizar uma reserva com menos de 5 diárias para imóveis do tipo Pousada";

        when(usuarioService.buscarPeloId(request.getIdSolicitante())).thenReturn(Usuario.builder().build());
        when(anuncioService.buscarPeloId(request.getIdAnuncio()))
                .thenReturn(Anuncio.builder()
                        .anunciante(Usuario.builder()
                                .id(2L).build())
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.POUSADA).build()).build());

        NumeroMinimoDiariasException e = assertThrows(NumeroMinimoDiariasException.class, ()->service.cadastrarReserva(request));
        assertEquals(msg, e.getMessage());
    }


    @Test
    public void deveRetornarErroSobreposicaoInicialRequestFinalExistente() throws UsuarioIdNaoExisteException, AnuncioNaoExisteException {
        CadastrarReservaRequest request = buildRequest();
        request.setPeriodo(Periodo.builder()
                .dataHoraInicial(LocalDateTime.of(2021,11,23,10,30,00))
                .dataHoraFinal(LocalDateTime.of(2021,11,26,23,45,00)).build());

        ArgumentCaptor<LocalDateTime> dataHoraInicialRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> dataHoraFinalRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);


        String msg = "Este anuncio já esta reservado para o período informado.";
        List<Reserva> reservas = new ArrayList<>();

        Reserva reservaExistente = Reserva.builder()
                .pagamento(Pagamento.builder().status(StatusPagamento.PAGO).build())
                .anuncio(Anuncio.builder()
                        .id(request.getIdAnuncio()).build())
                .periodo(Periodo.builder()
                        .dataHoraInicial(LocalDateTime.of(2021,11,19,14,00,00))
                        .dataHoraFinal(LocalDateTime.of(2021,11,25,12,00,00)).build())
                .build();

        reservas.add(reservaExistente);

        when(usuarioService.buscarPeloId(request.getIdSolicitante())).thenReturn(Usuario.builder().build());
        when(anuncioService.buscarPeloId(request.getIdAnuncio()))
                .thenReturn(Anuncio.builder()
                        .id(request.getIdAnuncio())
                        .anunciante(Usuario.builder()
                                .id(2L).build())
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.HOTEL).build()).build());

        when(repository.findAllByPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsGreaterThanAndPeriodo_DataHoraFinalIsLessThan(dataHoraInicialRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture()))
                .thenReturn(reservas);

        PeriodoInvalidoException e = assertThrows(PeriodoInvalidoException.class, ()->service.cadastrarReserva(request));
        assertEquals(request.getPeriodo().getDataHoraInicial(), dataHoraInicialRequestCaptor.getValue());
        assertEquals(request.getPeriodo().getDataHoraFinal(), dataHoraFinalRequestCaptor.getValue());
        assertEquals(msg, e.getMessage());

    }

    @Test
    public void deveRetornarErroSobreposicaoFinalRequestInicialExistente() throws UsuarioIdNaoExisteException, AnuncioNaoExisteException {
        CadastrarReservaRequest request = buildRequest();
        request.setPeriodo(Periodo.builder()
                .dataHoraInicial(LocalDateTime.of(2021,11,12,10,30,00))
                .dataHoraFinal(LocalDateTime.of(2021,11,20,23,45,00)).build());

        ArgumentCaptor<LocalDateTime> dataHoraInicialRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> dataHoraFinalRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);


        String msg = "Este anuncio já esta reservado para o período informado.";
        List<Reserva> reservas = new ArrayList<>();

        Reserva reservaExistente = Reserva.builder()
                .pagamento(Pagamento.builder().status(StatusPagamento.PAGO).build())
                .anuncio(Anuncio.builder()
                        .id(request.getIdAnuncio()).build())
                .periodo(Periodo.builder()
                        .dataHoraInicial(LocalDateTime.of(2021,11,19,14,00,00))
                        .dataHoraFinal(LocalDateTime.of(2021,11,25,12,00,00)).build())
                .build();

        reservas.add(reservaExistente);

        when(usuarioService.buscarPeloId(request.getIdSolicitante())).thenReturn(Usuario.builder().build());
        when(anuncioService.buscarPeloId(request.getIdAnuncio()))
                .thenReturn(Anuncio.builder()
                        .id(request.getIdAnuncio())
                        .anunciante(Usuario.builder()
                                .id(2L).build())
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.HOTEL).build()).build());

        when(repository.findAllByPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsGreaterThanAndPeriodo_DataHoraFinalIsLessThan(dataHoraInicialRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture()))
                .thenReturn(reservas);

        PeriodoInvalidoException e = assertThrows(PeriodoInvalidoException.class, ()->service.cadastrarReserva(request));
        assertEquals(request.getPeriodo().getDataHoraInicial(), dataHoraInicialRequestCaptor.getValue());
        assertEquals(request.getPeriodo().getDataHoraFinal(), dataHoraFinalRequestCaptor.getValue());
        assertEquals(msg, e.getMessage());

    }

    @Test
    public void deveRetornarErroAmbasForaSobreposicaoTotalExistente() throws UsuarioIdNaoExisteException, AnuncioNaoExisteException {
        CadastrarReservaRequest request = buildRequest();
        request.setPeriodo(Periodo.builder()
                .dataHoraInicial(LocalDateTime.of(2021,11,18,8,30,00))
                .dataHoraFinal(LocalDateTime.of(2021,11,25,23,45,00)).build());

        ArgumentCaptor<LocalDateTime> dataHoraInicialRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> dataHoraFinalRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);


        String msg = "Este anuncio já esta reservado para o período informado.";
        List<Reserva> reservas = new ArrayList<>();

        Reserva reservaExistente = Reserva.builder()
                .pagamento(Pagamento.builder().status(StatusPagamento.PAGO).build())
                .anuncio(Anuncio.builder()
                        .id(request.getIdAnuncio()).build())
                .periodo(Periodo.builder()
                        .dataHoraInicial(LocalDateTime.of(2021,11,19,14,00,00))
                        .dataHoraFinal(LocalDateTime.of(2021,11,25,12,00,00)).build())
                .build();

        reservas.add(reservaExistente);

        when(usuarioService.buscarPeloId(request.getIdSolicitante())).thenReturn(Usuario.builder().build());
        when(anuncioService.buscarPeloId(request.getIdAnuncio()))
                .thenReturn(Anuncio.builder()
                        .id(request.getIdAnuncio())
                        .anunciante(Usuario.builder()
                                .id(2L).build())
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.HOTEL).build()).build());

        when(repository.findAllByPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsGreaterThanAndPeriodo_DataHoraFinalIsLessThan(dataHoraInicialRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture()))
                .thenReturn(reservas);

        PeriodoInvalidoException e = assertThrows(PeriodoInvalidoException.class, ()->service.cadastrarReserva(request));
        assertEquals(request.getPeriodo().getDataHoraInicial(), dataHoraInicialRequestCaptor.getValue());
        assertEquals(request.getPeriodo().getDataHoraFinal(), dataHoraFinalRequestCaptor.getValue());
        assertEquals(msg, e.getMessage());

    }

    @Test
    public void deveRetornarErroAmbasDentroSobreposicaoTotalExistente() throws UsuarioIdNaoExisteException, AnuncioNaoExisteException {
        CadastrarReservaRequest request = buildRequest();
        request.setPeriodo(Periodo.builder()
                .dataHoraInicial(LocalDateTime.of(2021,11,21,8,30,00))
                .dataHoraFinal(LocalDateTime.of(2021,11,24,23,45,00)).build());

        ArgumentCaptor<LocalDateTime> dataHoraInicialRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> dataHoraFinalRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);


        String msg = "Este anuncio já esta reservado para o período informado.";
        List<Reserva> reservas = new ArrayList<>();

        Reserva reservaExistente = Reserva.builder()
                .pagamento(Pagamento.builder().status(StatusPagamento.PAGO).build())
                .anuncio(Anuncio.builder()
                        .id(request.getIdAnuncio()).build())
                .periodo(Periodo.builder()
                        .dataHoraInicial(LocalDateTime.of(2021,11,19,14,00,00))
                        .dataHoraFinal(LocalDateTime.of(2021,11,25,12,00,00)).build())
                .build();

        reservas.add(reservaExistente);

        when(usuarioService.buscarPeloId(request.getIdSolicitante())).thenReturn(Usuario.builder().build());
        when(anuncioService.buscarPeloId(request.getIdAnuncio()))
                .thenReturn(Anuncio.builder()
                        .id(request.getIdAnuncio())
                        .anunciante(Usuario.builder()
                                .id(2L).build())
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.HOTEL).build()).build());

        when(repository.findAllByPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsGreaterThanAndPeriodo_DataHoraFinalIsLessThan(dataHoraInicialRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture()))
                .thenReturn(reservas);

        PeriodoInvalidoException e = assertThrows(PeriodoInvalidoException.class, ()->service.cadastrarReserva(request));
        assertEquals(request.getPeriodo().getDataHoraInicial(), dataHoraInicialRequestCaptor.getValue());
        assertEquals(request.getPeriodo().getDataHoraFinal(), dataHoraFinalRequestCaptor.getValue());
        assertEquals(msg, e.getMessage());

    }

    @Test
    public void deveCriarReservaDataFinalRequestMesmaDataInicialExistente() throws UsuarioIdNaoExisteException, AnuncioNaoExisteException, SolicitanteIgualAnuncianteException, NumeroMinimoPessoasException, PeriodoInvalidoException, NumeroMinimoDiariasException {
        CadastrarReservaRequest request = buildRequest();
        request.setPeriodo(Periodo.builder()
                .dataHoraInicial(LocalDateTime.of(2021,11,25,8,30,00))
                .dataHoraFinal(LocalDateTime.of(2021,11,26,23,45,00)).build());

        ArgumentCaptor<LocalDateTime> dataHoraInicialRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> dataHoraFinalRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<Reserva> reservaArgumentCaptor  = ArgumentCaptor.forClass(Reserva.class);

        Periodo periodoAjustado = Periodo.builder()
                .dataHoraInicial(LocalDateTime.of(2021,11,25,14,00,00))
                .dataHoraFinal(LocalDateTime.of(2021,11,26,12,00,00)).build();

        Reserva reservaExpected = Reserva.builder()
                .periodo(periodoAjustado)
                .anuncio(Anuncio.builder()
                        .id(request.getIdAnuncio())
                        .valorDiaria(BigDecimal.valueOf(50000,2))
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.HOTEL).build())
                        .anunciante(Usuario.builder()
                                .id(2L).build()).build())
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.PENDENTE)
                        .valorTotal(BigDecimal.valueOf(50000,2))
                        .build())
                .quantidadePessoas(request.getQuantidadePessoas())
                .solicitante(Usuario.builder().build())
                .build();


        List<Reserva> reservas = new ArrayList<>();

        Reserva reservaExistente = Reserva.builder()
                .pagamento(Pagamento.builder().status(StatusPagamento.PAGO).build())
                .anuncio(Anuncio.builder()
                        .id(request.getIdAnuncio()).build())
                .periodo(Periodo.builder()
                        .dataHoraInicial(LocalDateTime.of(2021,11,19,14,00,00))
                        .dataHoraFinal(LocalDateTime.of(2021,11,25,12,00,00)).build())
                .build();

        reservas.add(reservaExistente);

        when(usuarioService.buscarPeloId(request.getIdSolicitante())).thenReturn(Usuario.builder().build());
        when(anuncioService.buscarPeloId(request.getIdAnuncio()))
                .thenReturn(Anuncio.builder()
                        .id(request.getIdAnuncio())
                        .anunciante(Usuario.builder()
                                .id(2L).build())
                        .valorDiaria(BigDecimal.valueOf(50000,2))
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.HOTEL).build()).build());

        when(repository.findAllByPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsGreaterThanAndPeriodo_DataHoraFinalIsLessThan(dataHoraInicialRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture()))
                .thenReturn(reservas);

        when(repository.save(reservaArgumentCaptor.capture())).thenReturn(reservaExpected);

        service.cadastrarReserva(request);


        assertEquals(request.getPeriodo().getDataHoraInicial(), dataHoraInicialRequestCaptor.getValue());
        assertEquals(request.getPeriodo().getDataHoraFinal(), dataHoraFinalRequestCaptor.getValue());
        assertEquals(reservaExpected, reservaArgumentCaptor.getValue());

    }

    @Test
    public void deveCriarReservaDataInicialRequestMesmaDataFinalExistente() throws UsuarioIdNaoExisteException, AnuncioNaoExisteException, SolicitanteIgualAnuncianteException, NumeroMinimoPessoasException, PeriodoInvalidoException, NumeroMinimoDiariasException {
        CadastrarReservaRequest request = buildRequest();
        request.setPeriodo(Periodo.builder()
                .dataHoraInicial(LocalDateTime.of(2021,11,18,8,30,00))
                .dataHoraFinal(LocalDateTime.of(2021,11,19,23,45,00)).build());

        ArgumentCaptor<LocalDateTime> dataHoraInicialRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> dataHoraFinalRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<Reserva> reservaArgumentCaptor  = ArgumentCaptor.forClass(Reserva.class);

        Periodo periodoAjustado = Periodo.builder()
                .dataHoraInicial(LocalDateTime.of(2021,11,18,14,00,00))
                .dataHoraFinal(LocalDateTime.of(2021,11,19,12,00,00)).build();

        Reserva reservaExpected = Reserva.builder()
                .periodo(periodoAjustado)
                .anuncio(Anuncio.builder()
                        .id(request.getIdAnuncio())
                        .valorDiaria(BigDecimal.valueOf(50000,2))
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.HOTEL).build())
                        .anunciante(Usuario.builder()
                                .id(2L).build()).build())
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.PENDENTE)
                        .valorTotal(BigDecimal.valueOf(50000,2))
                        .build())
                .quantidadePessoas(request.getQuantidadePessoas())
                .solicitante(Usuario.builder().build())
                .build();


        List<Reserva> reservas = new ArrayList<>();

        Reserva reservaExistente = Reserva.builder()
                .pagamento(Pagamento.builder().status(StatusPagamento.PAGO).build())
                .anuncio(Anuncio.builder()
                        .id(request.getIdAnuncio()).build())
                .periodo(Periodo.builder()
                        .dataHoraInicial(LocalDateTime.of(2021,11,19,14,00,00))
                        .dataHoraFinal(LocalDateTime.of(2021,11,25,12,00,00)).build())
                .build();

        reservas.add(reservaExistente);

        when(usuarioService.buscarPeloId(request.getIdSolicitante())).thenReturn(Usuario.builder().build());
        when(anuncioService.buscarPeloId(request.getIdAnuncio()))
                .thenReturn(Anuncio.builder()
                        .id(request.getIdAnuncio())
                        .anunciante(Usuario.builder()
                                .id(2L).build())
                        .valorDiaria(BigDecimal.valueOf(50000,2))
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.HOTEL).build()).build());

        when(repository.findAllByPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsGreaterThanAndPeriodo_DataHoraFinalIsLessThan(dataHoraInicialRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture()))
                .thenReturn(reservas);

        when(repository.save(reservaArgumentCaptor.capture())).thenReturn(reservaExpected);

        service.cadastrarReserva(request);


        assertEquals(request.getPeriodo().getDataHoraInicial(), dataHoraInicialRequestCaptor.getValue());
        assertEquals(request.getPeriodo().getDataHoraFinal(), dataHoraFinalRequestCaptor.getValue());
        assertEquals(reservaExpected, reservaArgumentCaptor.getValue());

    }

    @Test
    public void deveCriarReservaAmbasForaAposExistente() throws UsuarioIdNaoExisteException, AnuncioNaoExisteException, SolicitanteIgualAnuncianteException, NumeroMinimoPessoasException, PeriodoInvalidoException, NumeroMinimoDiariasException {
        CadastrarReservaRequest request = buildRequest();
        request.setPeriodo(Periodo.builder()
                .dataHoraInicial(LocalDateTime.of(2021,11,27,8,30,00))
                .dataHoraFinal(LocalDateTime.of(2021,11,29,23,45,00)).build());

        ArgumentCaptor<LocalDateTime> dataHoraInicialRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> dataHoraFinalRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<Reserva> reservaArgumentCaptor  = ArgumentCaptor.forClass(Reserva.class);

        Periodo periodoAjustado = Periodo.builder()
                .dataHoraInicial(LocalDateTime.of(2021,11,27,14,00,00))
                .dataHoraFinal(LocalDateTime.of(2021,11,29,12,00,00)).build();

        Reserva reservaExpected = Reserva.builder()
                .periodo(periodoAjustado)
                .anuncio(Anuncio.builder()
                        .id(request.getIdAnuncio())
                        .valorDiaria(BigDecimal.valueOf(50000,2))
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.HOTEL).build())
                        .anunciante(Usuario.builder()
                                .id(2L).build()).build())
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.PENDENTE)
                        .valorTotal(BigDecimal.valueOf(100000,2))
                        .build())
                .quantidadePessoas(request.getQuantidadePessoas())
                .solicitante(Usuario.builder().build())
                .build();


        List<Reserva> reservas = new ArrayList<>();

        Reserva reservaExistente = Reserva.builder()
                .pagamento(Pagamento.builder().status(StatusPagamento.PAGO).build())
                .anuncio(Anuncio.builder()
                        .id(request.getIdAnuncio()).build())
                .periodo(Periodo.builder()
                        .dataHoraInicial(LocalDateTime.of(2021,11,19,14,00,00))
                        .dataHoraFinal(LocalDateTime.of(2021,11,25,12,00,00)).build())
                .build();

        reservas.add(reservaExistente);

        when(usuarioService.buscarPeloId(request.getIdSolicitante())).thenReturn(Usuario.builder().build());
        when(anuncioService.buscarPeloId(request.getIdAnuncio()))
                .thenReturn(Anuncio.builder()
                        .id(request.getIdAnuncio())
                        .anunciante(Usuario.builder()
                                .id(2L).build())
                        .valorDiaria(BigDecimal.valueOf(50000,2))
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.HOTEL).build()).build());

        when(repository.findAllByPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsGreaterThanAndPeriodo_DataHoraFinalIsLessThan(dataHoraInicialRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture()))
                .thenReturn(reservas);

        when(repository.save(reservaArgumentCaptor.capture())).thenReturn(reservaExpected);

        service.cadastrarReserva(request);
        Reserva reservaCriada = reservaArgumentCaptor.getValue();

        assertEquals(request.getPeriodo().getDataHoraInicial(), dataHoraInicialRequestCaptor.getValue());
        assertEquals(request.getPeriodo().getDataHoraFinal(), dataHoraFinalRequestCaptor.getValue());
        assertEquals(reservaExpected, reservaArgumentCaptor.getValue());

    }

    @Test
    public void deveCriarReservaAmbasForaAntesExistente() throws UsuarioIdNaoExisteException, AnuncioNaoExisteException, SolicitanteIgualAnuncianteException, NumeroMinimoPessoasException, PeriodoInvalidoException, NumeroMinimoDiariasException {
        CadastrarReservaRequest request = buildRequest();
        request.setPeriodo(Periodo.builder()
                .dataHoraInicial(LocalDateTime.of(2021,11,15,8,30,00))
                .dataHoraFinal(LocalDateTime.of(2021,11,17,23,45,00)).build());

        ArgumentCaptor<LocalDateTime> dataHoraInicialRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> dataHoraFinalRequestCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<Reserva> reservaArgumentCaptor  = ArgumentCaptor.forClass(Reserva.class);

        Periodo periodoAjustado = Periodo.builder()
                .dataHoraInicial(LocalDateTime.of(2021,11,15,14,00,00))
                .dataHoraFinal(LocalDateTime.of(2021,11,17,12,00,00)).build();

        Reserva reservaExpected = Reserva.builder()
                .periodo(periodoAjustado)
                .anuncio(Anuncio.builder()
                        .id(request.getIdAnuncio())
                        .valorDiaria(BigDecimal.valueOf(50000,2))
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.HOTEL).build())
                        .anunciante(Usuario.builder()
                                .id(2L).build()).build())
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.PENDENTE)
                        .valorTotal(BigDecimal.valueOf(100000,2))
                        .build())
                .quantidadePessoas(request.getQuantidadePessoas())
                .solicitante(Usuario.builder().build())
                .build();


        List<Reserva> reservas = new ArrayList<>();

        Reserva reservaExistente = Reserva.builder()
                .pagamento(Pagamento.builder().status(StatusPagamento.PAGO).build())
                .anuncio(Anuncio.builder()
                        .id(request.getIdAnuncio()).build())
                .periodo(Periodo.builder()
                        .dataHoraInicial(LocalDateTime.of(2021,11,19,14,00,00))
                        .dataHoraFinal(LocalDateTime.of(2021,11,25,12,00,00)).build())
                .build();

        reservas.add(reservaExistente);

        when(usuarioService.buscarPeloId(request.getIdSolicitante())).thenReturn(Usuario.builder().build());
        when(anuncioService.buscarPeloId(request.getIdAnuncio()))
                .thenReturn(Anuncio.builder()
                        .id(request.getIdAnuncio())
                        .anunciante(Usuario.builder()
                                .id(2L).build())
                        .valorDiaria(BigDecimal.valueOf(50000,2))
                        .imovel(Imovel.builder()
                                .tipoImovel(TipoImovel.HOTEL).build()).build());

        when(repository.findAllByPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsGreaterThanAndPeriodo_DataHoraFinalIsLessThan(dataHoraInicialRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture(), dataHoraInicialRequestCaptor.capture(),dataHoraFinalRequestCaptor.capture()))
                .thenReturn(reservas);

        when(repository.save(reservaArgumentCaptor.capture())).thenReturn(reservaExpected);

        service.cadastrarReserva(request);
        Reserva reservaCriada = reservaArgumentCaptor.getValue();

        assertEquals(request.getPeriodo().getDataHoraInicial(), dataHoraInicialRequestCaptor.getValue());
        assertEquals(request.getPeriodo().getDataHoraFinal(), dataHoraFinalRequestCaptor.getValue());
        assertEquals(reservaExpected, reservaArgumentCaptor.getValue());

    }




}
