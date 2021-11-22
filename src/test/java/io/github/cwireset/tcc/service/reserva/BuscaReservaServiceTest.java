package io.github.cwireset.tcc.service.reserva;

import io.github.cwireset.tcc.repository.ReservaRepository;
import io.github.cwireset.tcc.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    public void deveRetornarPageVaziaQuandoPeriodoNaoPassado(){


    }
}
