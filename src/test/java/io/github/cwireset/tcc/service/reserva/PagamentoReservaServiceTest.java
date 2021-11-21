package io.github.cwireset.tcc.service.reserva;


import io.github.cwireset.tcc.domain.*;
import io.github.cwireset.tcc.exception.FormaPagaMentoInvalidaException;
import io.github.cwireset.tcc.exception.ReservaNaoExisteException;
import io.github.cwireset.tcc.exception.ReservaNaoPagaException;
import io.github.cwireset.tcc.exception.ReservaNaoPendenteException;
import io.github.cwireset.tcc.repository.ReservaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PagamentoReservaServiceTest {

    @InjectMocks
    PagamentoReservaService service;
    @Mock
    ReservaRepository repository;

    @BeforeEach
    void setUp(){

    }

    @Test
    public void deveRetornarErroAoNaoEncontrarReserva() {
        Long idReserva = 1L;
        String msg = String.format("Nenhum(a) Reserva com Id com o valor '%s' foi encontrado.",idReserva);

        when(repository.existsById(idReserva)).thenReturn(false);

        ReservaNaoExisteException e = Assertions.assertThrows(ReservaNaoExisteException.class, ()->service.pagar(idReserva, FormaPagamento.CARTAO_DEBITO));
        Assertions.assertEquals(msg, e.getMessage());
    }

    @Test
    public void deveRetornarErroFormaPagamentoNaoAceita() {
        Long idReserva = 1L;
        List<FormaPagamento> formasAceitas = new ArrayList<>();
        formasAceitas.add(FormaPagamento.DINHEIRO);
        formasAceitas.add(FormaPagamento.CARTAO_CREDITO);

        Anuncio anuncio = Anuncio.builder()
                .formasAceitas(formasAceitas)
                .build();

        Reserva expected = Reserva.builder()
                .anuncio(anuncio).build();

        String msg = String.format("O anúncio não aceita PIX como forma de pagamento. As formas aceitas são DINHEIRO, CARTAO_CREDITO.");

        when(repository.existsById(idReserva)).thenReturn(true);
        when(repository.findById(idReserva)).thenReturn(java.util.Optional.ofNullable(expected));

        FormaPagaMentoInvalidaException e = Assertions.assertThrows(FormaPagaMentoInvalidaException.class, ()->service.pagar(idReserva, FormaPagamento.PIX));
        Assertions.assertEquals(msg, e.getMessage());
    }

    @Test
    public void deveRetornarErroAoPagarReservaPaga() {
        Long idReserva = 1L;
        List<FormaPagamento> formasAceitas = new ArrayList<>();
        formasAceitas.add(FormaPagamento.PIX);
        formasAceitas.add(FormaPagamento.CARTAO_DEBITO);

        Anuncio anuncio = Anuncio.builder()
                .formasAceitas(formasAceitas)
                .build();

        Reserva expected = Reserva.builder()
                .anuncio(anuncio)
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.PAGO).build()).build();

        String msg = String.format("Não é possível realizar o pagamento para esta reserva, pois ela não está no status PENDENTE.");

        when(repository.existsById(idReserva)).thenReturn(true);
        when(repository.findById(idReserva)).thenReturn(java.util.Optional.ofNullable(expected));

        ReservaNaoPendenteException e = Assertions.assertThrows(ReservaNaoPendenteException.class, ()->service.pagar(idReserva, FormaPagamento.PIX));
        Assertions.assertEquals(msg, e.getMessage());
    }

    @Test
    public void deveRetornarErroAoPagarReservaCancelada() {
        Long idReserva = 1L;
        List<FormaPagamento> formasAceitas = new ArrayList<>();
        formasAceitas.add(FormaPagamento.PIX);
        formasAceitas.add(FormaPagamento.CARTAO_DEBITO);

        Anuncio anuncio = Anuncio.builder()
                .formasAceitas(formasAceitas)
                .build();

        Reserva expected = Reserva.builder()
                .anuncio(anuncio)
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.CANCELADO).build()).build();

        String msg = String.format("Não é possível realizar o pagamento para esta reserva, pois ela não está no status PENDENTE.");

        when(repository.existsById(idReserva)).thenReturn(true);
        when(repository.findById(idReserva)).thenReturn(java.util.Optional.ofNullable(expected));

        ReservaNaoPendenteException e = Assertions.assertThrows(ReservaNaoPendenteException.class, ()->service.pagar(idReserva, FormaPagamento.PIX));
        Assertions.assertEquals(msg, e.getMessage());
    }

    @Test
    public void deveRetornarErroAoPagarReservaEstornada() {
        Long idReserva = 1L;
        List<FormaPagamento> formasAceitas = new ArrayList<>();
        formasAceitas.add(FormaPagamento.PIX);
        formasAceitas.add(FormaPagamento.CARTAO_DEBITO);

        Anuncio anuncio = Anuncio.builder()
                .formasAceitas(formasAceitas)
                .build();

        Reserva expected = Reserva.builder()
                .anuncio(anuncio)
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.ESTORNADO).build()).build();

        String msg = String.format("Não é possível realizar o pagamento para esta reserva, pois ela não está no status PENDENTE.");

        when(repository.existsById(idReserva)).thenReturn(true);
        when(repository.findById(idReserva)).thenReturn(java.util.Optional.ofNullable(expected));

        ReservaNaoPendenteException e = Assertions.assertThrows(ReservaNaoPendenteException.class, ()->service.pagar(idReserva, FormaPagamento.PIX));
        Assertions.assertEquals(msg, e.getMessage());
    }

    @Test
    public void devePagarReserva() throws FormaPagaMentoInvalidaException, ReservaNaoPendenteException, ReservaNaoExisteException {
        Long idReserva = 1L;
        List<FormaPagamento> formasAceitas = new ArrayList<>();
        formasAceitas.add(FormaPagamento.PIX);
        formasAceitas.add(FormaPagamento.CARTAO_DEBITO);
        ArgumentCaptor<Reserva> argumentCaptor = ArgumentCaptor.forClass(Reserva.class);

        Anuncio anuncio = Anuncio.builder()
                .formasAceitas(formasAceitas)
                .build();

        Reserva reserva = Reserva.builder()
                .anuncio(anuncio)
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.PENDENTE)
                        .build()).build();

        Reserva expected = Reserva.builder()
                .anuncio(anuncio)
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.PAGO)
                        .formaEscolhida(FormaPagamento.PIX).build()).build();

        when(repository.existsById(idReserva)).thenReturn(true);
        when(repository.findById(idReserva)).thenReturn(java.util.Optional.ofNullable(reserva));
        when(repository.save(argumentCaptor.capture())).thenReturn(expected);
        service.pagar(idReserva,FormaPagamento.PIX);

        Assertions.assertEquals(argumentCaptor.getValue(),expected);
    }

    //CANCELAR
    @Test
    public void deveRetornarErroAoNaoEncontrarReservaCancelar() {
        Long idReserva = 1L;
        String msg = String.format("Nenhum(a) Reserva com Id com o valor '%s' foi encontrado.",idReserva);

        when(repository.existsById(idReserva)).thenReturn(false);

        ReservaNaoExisteException e = Assertions.assertThrows(ReservaNaoExisteException.class, ()->service.cancelar(idReserva));
        Assertions.assertEquals(msg, e.getMessage());
    }


    @Test
    public void deveRetornarErroAoCancelarReservaPaga() {
        Long idReserva = 1L;

        Reserva expected = Reserva.builder()
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.PAGO).build()).build();

        String msg = String.format("Não é possível realizar o cancelamento para esta reserva, pois ela não está no status PENDENTE.");

        when(repository.existsById(idReserva)).thenReturn(true);
        when(repository.findById(idReserva)).thenReturn(java.util.Optional.ofNullable(expected));

        ReservaNaoPendenteException e = Assertions.assertThrows(ReservaNaoPendenteException.class, ()->service.cancelar(idReserva));
        Assertions.assertEquals(msg, e.getMessage());
    }

    @Test
    public void deveRetornarErroAoCancelarReservaCancelada() {
        Long idReserva = 1L;
        Reserva expected = Reserva.builder()
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.CANCELADO).build()).build();

        String msg = String.format("Não é possível realizar o cancelamento para esta reserva, pois ela não está no status PENDENTE.");

        when(repository.existsById(idReserva)).thenReturn(true);
        when(repository.findById(idReserva)).thenReturn(java.util.Optional.ofNullable(expected));

        ReservaNaoPendenteException e = Assertions.assertThrows(ReservaNaoPendenteException.class, ()->service.cancelar(idReserva));
        Assertions.assertEquals(msg, e.getMessage());
    }

    @Test
    public void deveRetornarErroAoCancelarReservaEstornada() {
        Long idReserva = 1L;
        Reserva expected = Reserva.builder()
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.ESTORNADO).build()).build();

        String msg = String.format("Não é possível realizar o cancelamento para esta reserva, pois ela não está no status PENDENTE.");

        when(repository.existsById(idReserva)).thenReturn(true);
        when(repository.findById(idReserva)).thenReturn(java.util.Optional.ofNullable(expected));

        ReservaNaoPendenteException e = Assertions.assertThrows(ReservaNaoPendenteException.class, ()->service.cancelar(idReserva));
        Assertions.assertEquals(msg, e.getMessage());
    }

    @Test
    public void deveCancelarReserva() throws ReservaNaoPendenteException, ReservaNaoExisteException {
        Long idReserva = 1L;
        ArgumentCaptor<Reserva> argumentCaptor = ArgumentCaptor.forClass(Reserva.class);
        Reserva reserva = Reserva.builder()
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.PENDENTE).build()).build();

        Reserva expected = Reserva.builder()
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.CANCELADO)
                        .build()).build();

        when(repository.existsById(idReserva)).thenReturn(true);
        when(repository.findById(idReserva)).thenReturn(java.util.Optional.ofNullable(reserva));
        when(repository.save(argumentCaptor.capture())).thenReturn(expected);
        service.cancelar(idReserva);

        Assertions.assertEquals(argumentCaptor.getValue(),expected);
    }

    //ESTORNAR
    @Test
    public void deveRetornarErroAoNaoEncontrarReservaEstornar() {
        Long idReserva = 1L;
        String msg = String.format("Nenhum(a) Reserva com Id com o valor '%s' foi encontrado.",idReserva);

        when(repository.existsById(idReserva)).thenReturn(false);

        ReservaNaoExisteException e = Assertions.assertThrows(ReservaNaoExisteException.class, ()->service.estornar(idReserva));
        Assertions.assertEquals(msg, e.getMessage());
    }


    @Test
    public void deveRetornarErroAoEstornarReservaPendente() {
        Long idReserva = 1L;

        Reserva expected = Reserva.builder()
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.PENDENTE).build()).build();

        String msg = String.format("Não é possível realizar o estorno para esta reserva, pois ela não está no status PAGO.");

        when(repository.existsById(idReserva)).thenReturn(true);
        when(repository.findById(idReserva)).thenReturn(java.util.Optional.ofNullable(expected));

        ReservaNaoPagaException e = Assertions.assertThrows(ReservaNaoPagaException.class, ()->service.estornar(idReserva));
        Assertions.assertEquals(msg, e.getMessage());
    }

    @Test
    public void deveRetornarErroAoEstornarReservaCancelada() {
        Long idReserva = 1L;
        Reserva expected = Reserva.builder()
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.CANCELADO).build()).build();

        String msg = String.format("Não é possível realizar o estorno para esta reserva, pois ela não está no status PAGO.");

        when(repository.existsById(idReserva)).thenReturn(true);
        when(repository.findById(idReserva)).thenReturn(java.util.Optional.ofNullable(expected));

        ReservaNaoPagaException e = Assertions.assertThrows(ReservaNaoPagaException.class, ()->service.estornar(idReserva));
        Assertions.assertEquals(msg, e.getMessage());
    }

    @Test
    public void deveRetornarErroAoEstornarReservaEstornada() {
        Long idReserva = 1L;
        Reserva expected = Reserva.builder()
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.ESTORNADO).build()).build();

        String msg = String.format("Não é possível realizar o estorno para esta reserva, pois ela não está no status PAGO.");

        when(repository.existsById(idReserva)).thenReturn(true);
        when(repository.findById(idReserva)).thenReturn(java.util.Optional.ofNullable(expected));

        ReservaNaoPagaException e = Assertions.assertThrows(ReservaNaoPagaException.class, ()->service.estornar(idReserva));
        Assertions.assertEquals(msg, e.getMessage());
    }

    @Test
    public void deveEstornarReserva() throws ReservaNaoPendenteException, ReservaNaoExisteException, ReservaNaoPagaException {
        Long idReserva = 1L;
        ArgumentCaptor<Reserva> argumentCaptor = ArgumentCaptor.forClass(Reserva.class);
        Reserva reserva = Reserva.builder()
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.PAGO).build()).build();

        Reserva expected = Reserva.builder()
                .pagamento(Pagamento.builder()
                        .status(StatusPagamento.ESTORNADO)
                        .build()).build();

        when(repository.existsById(idReserva)).thenReturn(true);
        when(repository.findById(idReserva)).thenReturn(java.util.Optional.ofNullable(reserva));
        when(repository.save(argumentCaptor.capture())).thenReturn(expected);
        service.estornar(idReserva);

        Assertions.assertEquals(argumentCaptor.getValue(),expected);
    }
}
