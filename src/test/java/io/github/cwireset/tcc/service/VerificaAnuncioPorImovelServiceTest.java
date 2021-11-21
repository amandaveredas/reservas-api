package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.*;
import io.github.cwireset.tcc.repository.AnuncioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VerificaAnuncioPorImovelServiceTest {

    @InjectMocks
    private VerificaAnuncioPorImovelService service;

    @Mock
    private AnuncioRepository repository;

    @Captor
    private ArgumentCaptor<Imovel> argumentCaptor;

    @BeforeEach
    void setUp(){

    }

    private Imovel buildImovel(){
        final Endereco endereco = Endereco.builder()
                .id(1L)
                .logradouro("Rua unicornio")
                .numero("200")
                .complemento("110")
                .bairro("Forever Young")
                .estado("Califórnia")
                .cidade("Los Angeles")
                .cep("29987-785")
                .build();

        final Usuario usuario = Usuario.builder()
                .id(1L)
                .cpf("99999999999")
                .endereco(endereco)
                .dataNascimento(LocalDate.of(1989,11,06))
                .email("amanda@teste.com")
                .nome("Amanda")
                .senha("123456")
                .build();

        final List<CaracteristicaImovel> caracteristicas = new ArrayList<>();
        caracteristicas.add(new CaracteristicaImovel(1L, "ventilado"));
        caracteristicas.add(new CaracteristicaImovel(2L,"ensolarado"));

        final Imovel imovel = Imovel.builder()
                .id(1L)
                .ativo(true)
                .proprietario(usuario)
                .caracteristicas(caracteristicas)
                .identificacao("hotel em anchieta")
                .tipoImovel(TipoImovel.HOTEL)
                .endereco(endereco)
                .build();

        return imovel;
    }

    @Test
    public void deveEncontrarAnuncio() {
        Imovel imovel = buildImovel();

        when(repository.existsByImovelAndAtivoIsTrue(argumentCaptor.capture())).thenReturn(true);
        service.verificaSeExisteAnuncioParaImovel(imovel);

        Assertions.assertEquals(imovel, argumentCaptor.getValue());

    }
}
