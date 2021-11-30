package com.github.amanda.reservas.api.service.reserva;

import com.github.amanda.reservas.api.domain.Periodo;
import com.github.amanda.reservas.api.domain.Reserva;
import com.github.amanda.reservas.api.domain.Usuario;
import com.github.amanda.reservas.api.exception.UsuarioIdNaoExisteException;
import com.github.amanda.reservas.api.repository.ReservaRepository;
import com.github.amanda.reservas.api.service.UsuarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BuscaReservaServiceTest {

    @InjectMocks
    BuscaReservaService service;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private ReservaRepository repository;

    @BeforeEach
    void setUo(){

    }

    @Test
    public void deveRetornarPageComTodasReservasQuandoPeriodoNaoPassado() throws UsuarioIdNaoExisteException {
        Long idSolicitante = 1L;
        Pageable pageable = PageRequest.of(0,10, Sort.by(Sort.Direction.DESC,"periodo_dataHoraFinal"));
        ArgumentCaptor<Usuario> usuarioArgumentCaptor = ArgumentCaptor.forClass(Usuario.class);
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        Usuario solicitante = Usuario.builder()
                .id(1L).build();

        Page<Reserva> page = null;

        Periodo periodo = Periodo.builder().build();

        when(usuarioService.buscarPeloId(idSolicitante)).thenReturn(solicitante);
        when(repository.findAllBySolicitante(usuarioArgumentCaptor.capture(), pageableArgumentCaptor.capture())).thenReturn(page);
        service.buscarReservasPorSolicitante(idSolicitante,periodo,pageable);

        Assertions.assertEquals(usuarioArgumentCaptor.getValue(),solicitante);
        Assertions.assertEquals(pageableArgumentCaptor.getValue(),pageable);
    }

    @Test
    public void deveRetornarPageComTodasReservasQuandoDataInicioNaoPassada() throws UsuarioIdNaoExisteException {
        Long idSolicitante = 1L;
        Pageable pageable = PageRequest.of(0,10, Sort.by(Sort.Direction.DESC,"periodo_dataHoraFinal"));
        ArgumentCaptor<Usuario> usuarioArgumentCaptor = ArgumentCaptor.forClass(Usuario.class);
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        Usuario solicitante = Usuario.builder()
                .id(1L).build();

        Page<Reserva> page = null;

        Periodo periodo = Periodo.builder()
                .dataHoraFinal(LocalDateTime.of(2021,11,11,10,00,00)).build();

        when(usuarioService.buscarPeloId(idSolicitante)).thenReturn(solicitante);
        when(repository.findAllBySolicitante(usuarioArgumentCaptor.capture(), pageableArgumentCaptor.capture())).thenReturn(page);
        service.buscarReservasPorSolicitante(idSolicitante,periodo,pageable);

        Assertions.assertEquals(usuarioArgumentCaptor.getValue(),solicitante);
        Assertions.assertEquals(pageableArgumentCaptor.getValue(),pageable);
    }

    @Test
    public void deveRetornarPageComTodasReservasQuandoDataFinalNaoPassada() throws UsuarioIdNaoExisteException {
        Long idSolicitante = 1L;
        Pageable pageable = PageRequest.of(0,10, Sort.by(Sort.Direction.DESC,"periodo_dataHoraFinal"));
        ArgumentCaptor<Usuario> usuarioArgumentCaptor = ArgumentCaptor.forClass(Usuario.class);
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        Usuario solicitante = Usuario.builder()
                .id(1L).build();

        Page<Reserva> page = null;

        Periodo periodo = Periodo.builder()
                .dataHoraInicial(LocalDateTime.of(2021,11,11,10,00,00)).build();

        when(usuarioService.buscarPeloId(idSolicitante)).thenReturn(solicitante);
        when(repository.findAllBySolicitante(usuarioArgumentCaptor.capture(), pageableArgumentCaptor.capture())).thenReturn(page);
        service.buscarReservasPorSolicitante(idSolicitante,periodo,pageable);

        Assertions.assertEquals(usuarioArgumentCaptor.getValue(),solicitante);
        Assertions.assertEquals(pageableArgumentCaptor.getValue(),pageable);
    }

    @Test
    public void deveRetornarPageComReservasQuandoAnuncianteExistir() throws UsuarioIdNaoExisteException {
        Long idAnunciante = 1L;
        Pageable pageable = PageRequest.of(0,10, Sort.by(Sort.Direction.DESC,"periodo_dataHoraFinal"));
        ArgumentCaptor<Usuario> usuarioArgumentCaptor = ArgumentCaptor.forClass(Usuario.class);
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        Usuario anunciante = Usuario.builder()
                .id(1L).build();
        Page<Reserva> page = null;

        when(usuarioService.buscarPeloId(idAnunciante)).thenReturn(anunciante);
        when(repository.findReservasByAnuncio_Anunciante(usuarioArgumentCaptor.capture(), pageableArgumentCaptor.capture())).thenReturn(page);
        service.buscarReservasPorAnunciante(idAnunciante,pageable);

        Assertions.assertEquals(usuarioArgumentCaptor.getValue(),anunciante);
        Assertions.assertEquals(pageableArgumentCaptor.getValue(),pageable);
    }
}
