package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.*;
import io.github.cwireset.tcc.exception.*;
import io.github.cwireset.tcc.repository.AnuncioRepository;
import io.github.cwireset.tcc.request.CadastrarAnuncioRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnuncioServiceTest {

    @InjectMocks
    AnuncioService service;

    @Mock
    AnuncioRepository repository;

    @Mock
    ImovelService imovelService;

    @Mock
    UsuarioService usuarioService;

    @Mock
    VerificaAnuncioPorImovelService verificaAnuncioPorImovelService;

    @Captor
    ArgumentCaptor<Anuncio> anuncioArgumentCaptor;

    @BeforeEach
    void setUp(){

    }

    private CadastrarAnuncioRequest buildCadastrarAnuncioRequest(){

        List<FormaPagamento> formasAceitas = new ArrayList<>();
        formasAceitas.add(FormaPagamento.CARTAO_DEBITO);
        formasAceitas.add(FormaPagamento.PIX);

        return CadastrarAnuncioRequest.builder()
                .descricao("Casa no lago")
                .tipoAnuncio(TipoAnuncio.COMPLETO)
                .formasAceitas(formasAceitas)
                .idAnunciante(1L)
                .idImovel(1L)
                .valorDiaria(BigDecimal.valueOf(750.00))
                .build();
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
    public void deveRetornarErroCamposObrigatoriosNaoPreenchidos() {
        CadastrarAnuncioRequest cadastrarAnuncioRequest = buildCadastrarAnuncioRequest();
        cadastrarAnuncioRequest.setTipoAnuncio(null);
        cadastrarAnuncioRequest.setIdAnunciante(null);
        cadastrarAnuncioRequest.setDescricao(null);
        cadastrarAnuncioRequest.setFormasAceitas(null);
        cadastrarAnuncioRequest.setIdImovel(null);
        cadastrarAnuncioRequest.setValorDiaria(null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<CadastrarAnuncioRequest>> violations = validator.validate(cadastrarAnuncioRequest);

        assertEquals(6, violations.size());

    }

    @Test
    public void deveRetornarErroAoCadastrarUmAnuncioCujoImovelNaoExiste() throws ImovelIdNaoExisteException {
        Long id = 1L;
        CadastrarAnuncioRequest cadastrarAnuncioRequest = buildCadastrarAnuncioRequest();
        ImovelIdNaoExisteException  e = new ImovelIdNaoExisteException(id);
        String mensagemEsperada = String.format("Nenhum(a) Imovel.java com Id com o valor '%s' foi encontrado.",cadastrarAnuncioRequest.getIdImovel());

        when(imovelService.buscarPeloId(id)).thenThrow(ImovelIdNaoExisteException.class);

        assertThrows(ImovelIdNaoExisteException.class, ()->service.cadastrar(cadastrarAnuncioRequest));
        assertEquals(mensagemEsperada, e.getMessage());
    }

    @Test
    public void deveRetornarErroAoCadastrarAnuncioComAnuncianteNaoEncontrado() throws UsuarioIdNaoExisteException {
        Long idAnunciante = 1L;
        CadastrarAnuncioRequest cadastrarAnuncioRequest = buildCadastrarAnuncioRequest();
        UsuarioIdNaoExisteException e = new UsuarioIdNaoExisteException(idAnunciante);
        String mensagemEsperada = String.format("Nenhum(a) Usuario com Id com o valor '%s' foi encontrado.",cadastrarAnuncioRequest.getIdAnunciante());

        when(usuarioService.buscarPeloId(idAnunciante)).thenThrow(UsuarioIdNaoExisteException.class);

        assertThrows(UsuarioIdNaoExisteException.class, () -> service.cadastrar(cadastrarAnuncioRequest));
        assertEquals(mensagemEsperada, e.getMessage());
    }

    @Test
    public void deveRetornarErroAoCadastrarAnuncioDeUmImovelQueJaPossuaAnuncio() throws ImovelIdNaoExisteException, UsuarioIdNaoExisteException {
        Imovel imovel = buildImovel();
        imovel.setId(1L);
        CadastrarAnuncioRequest cadastrarAnuncioRequest = buildCadastrarAnuncioRequest();

        String mensagemEsperada = String.format("Já existe um recurso do tipo Anuncio com IdImovel com o valor '%s'.",imovel.getId());

        when(imovelService.buscarPeloId(imovel.getId())).thenReturn(imovel);
        when(usuarioService.buscarPeloId(cadastrarAnuncioRequest.getIdAnunciante())).thenReturn(Usuario.builder().build());
        when(verificaAnuncioPorImovelService.verificaSeExisteAnuncioParaImovel(imovel)).thenReturn(true);
        ImovelAmbiguidadeAnunciosException e = assertThrows(ImovelAmbiguidadeAnunciosException.class, ()->service.cadastrar(cadastrarAnuncioRequest));

        assertEquals(mensagemEsperada, e.getMessage());

    }

    @Test
    public void deveCadastrarUmAnuncio() throws ImovelIdNaoExisteException, UsuarioIdNaoExisteException, ImovelAmbiguidadeAnunciosException {
        Imovel imovel = buildImovel();
        imovel.setId(1L);
        CadastrarAnuncioRequest cadastrarAnuncioRequest = buildCadastrarAnuncioRequest();
        Usuario anunciante = Usuario.builder()
                .id(1L)
                .nome("Amanda")
                .email("amanda@teste.com")
                .senha("123")
                .cpf("11111111111")
                .dataNascimento(LocalDate.of(2000,10,10))
                .build();

        Anuncio expected = new Anuncio(1L,cadastrarAnuncioRequest.getTipoAnuncio(),imovel,anunciante,cadastrarAnuncioRequest.getValorDiaria(),cadastrarAnuncioRequest.getFormasAceitas(),cadastrarAnuncioRequest.getDescricao(),true);

        when(imovelService.buscarPeloId(cadastrarAnuncioRequest.getIdImovel())).thenReturn(imovel);
        when(usuarioService.buscarPeloId(cadastrarAnuncioRequest.getIdAnunciante())).thenReturn(anunciante);
        when(verificaAnuncioPorImovelService.verificaSeExisteAnuncioParaImovel(imovel)).thenReturn(false);
        when(repository.save(anuncioArgumentCaptor.capture())).thenReturn(expected);

        Anuncio anuncio = service.cadastrar(cadastrarAnuncioRequest);

        assertEquals(expected, anuncio);

    }

    @Test
    public void deveRetornarPaginaVazia() {
        Pageable pageable;
        pageable = PageRequest.of(0,10, Sort.by(Sort.Direction.DESC,"valorDiaria"));
        List<Anuncio> anuncios = new ArrayList<>();
        Page<Anuncio> expected = new PageImpl<>(anuncios);

        when(repository.findAllByAtivoIsTrue(pageable)).thenReturn(expected);

        assertEquals(0,service.listarTodos(pageable).getTotalElements());
    }

    @Test
    public void deveRetornarPaginaOrdenada() {
        Pageable pageable = PageRequest.of(0,5,Sort.by(Sort.Direction.DESC,"valorDiaria"));
        List<Anuncio> anuncios = new ArrayList<>();

        Anuncio anuncio1 = Anuncio.builder().build();
        anuncio1.setValorDiaria(BigDecimal.valueOf(950.00));
        anuncios.add(anuncio1);

        Anuncio anuncio2 = Anuncio.builder().build();
        anuncio1.setValorDiaria(BigDecimal.valueOf(450.00));
        anuncios.add(anuncio2);

        Page<Anuncio> expected = new PageImpl<Anuncio>(anuncios,pageable,anuncios.size());

        when(repository.findAllByAtivoIsTrue(pageable)).thenReturn(expected);
        Page<Anuncio> recebidos = service.listarTodos(pageable);

        assertEquals(2,recebidos.getTotalElements());
        assertEquals(1,recebidos.getTotalPages());
        assertEquals(5,recebidos.getSize());
        assertEquals(Sort.by(Sort.Direction.DESC,"valorDiaria"),recebidos.getSort());
        assertEquals(service.listarTodos(pageable), expected);

    }

    @Test
    public void deveRetornarPageVaziaQuandoAnuncianteNaoExistir() throws UsuarioIdNaoExisteException {
        Long id = 1L;
        Usuario anunciante = null;
        Pageable pageable = PageRequest.of(0,5,Sort.by(Sort.Direction.DESC,"valorDiaria"));
        List<Anuncio> anuncios = new ArrayList<>();
        Page<Anuncio> expected = new PageImpl<Anuncio>(anuncios,pageable,anuncios.size());

        when(usuarioService.buscarPeloId(id)).thenThrow(UsuarioIdNaoExisteException.class);
        when(repository.findAllByAtivoIsTrueAndAndAnuncianteEquals(anunciante,pageable)).thenReturn(expected);

        assertTrue(service.buscarAnunciosPorAnunciante(id,pageable).isEmpty());
    }

    @Test
    public void deveRetornarPageComAnunciosDoAnuncianteImoveisDoProprietario() throws UsuarioIdNaoExisteException {
        Long id = 1L;
        Usuario anunciante = new Usuario();
        Pageable pageable = PageRequest.of(0,5,Sort.by(Sort.Direction.ASC,"identificacao"));
        List<Anuncio> anuncios = new ArrayList<>();

        Anuncio anuncio1 = Anuncio.builder().build();
        anuncio1.setValorDiaria(BigDecimal.valueOf(950.00));
        anuncios.add(anuncio1);

        Anuncio anuncio2 = Anuncio.builder().build();
        anuncio1.setValorDiaria(BigDecimal.valueOf(450.00));
        anuncios.add(anuncio2);

        Page<Anuncio> expected = new PageImpl<Anuncio>(anuncios,pageable,anuncios.size());

        when(usuarioService.buscarPeloId(id)).thenReturn(anunciante);
        when(repository.findAllByAtivoIsTrueAndAndAnuncianteEquals(anunciante,pageable)).thenReturn(expected);

        assertEquals(2, service.buscarAnunciosPorAnunciante(id,pageable).getTotalElements());
    }

    @Test
    public void deveRetornarErroAoNaoEncontrarAnuncioPeloId(){
        Long id = 2L;
        String mensagemesperada = String.format("Nenhum(a) Anuncio com Id com o valor '%s' foi encontrado.",id);
        when(repository.existsByIdAndAtivoIsTrue(id)).thenReturn(false);

        AnuncioNaoExisteException e = assertThrows(AnuncioNaoExisteException.class, ()->service.buscarPeloId(id));

        assertEquals(mensagemesperada, e.getMessage());
    }

    @Test
    public void deveExcluirAnuncio() throws AnuncioNaoExisteException {
        Long id = 1L;
        Anuncio anuncio = Anuncio.builder().build();
        anuncio.setId(1L);

        when(repository.existsByIdAndAtivoIsTrue(id)).thenReturn(true);
        when(repository.findById(id)).thenReturn(java.util.Optional.ofNullable(anuncio));

        service.excluirLogicamente(anuncio.getId());

        assertFalse(anuncio.isAtivo());
    }

    @Test
    public void deveRetornarAnuncioEncontradoPeloId() throws AnuncioNaoExisteException {
        Long id = 1L;
        Anuncio expected = Anuncio.builder().build();
        expected.setId(1L);

        when(repository.findById(id)).thenReturn(java.util.Optional.ofNullable(expected));
        when(repository.existsByIdAndAtivoIsTrue(id)).thenReturn(true);

        Anuncio anuncio = service.buscarPeloId(id);

        assertEquals(anuncio,expected);

    }

}
