package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.*;
import io.github.cwireset.tcc.exception.ImovelIdNaoExisteException;
import io.github.cwireset.tcc.exception.ImovelPossuiAnuncioException;
import io.github.cwireset.tcc.exception.UsuarioIdNaoExisteException;
import io.github.cwireset.tcc.repository.ImovelRepository;
import io.github.cwireset.tcc.request.CadastrarImovelRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImovelServiceTest {

    @InjectMocks
    private ImovelService service;

    @Mock
    private ImovelRepository repository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private VerificaAnuncioPorImovelService verificaAnuncioPorImovelService;


    @BeforeEach
    void setUp(){

    }

    private CadastrarImovelRequest buildCadastrarImovelRequest () {
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

        final CadastrarImovelRequest cadastrarImovelRequest = CadastrarImovelRequest.builder()
                .idProprietario(1L)
                .identificacao("hotel em anchieta")
                .tipoImovel(TipoImovel.HOTEL)
                .endereco(endereco).build();

        return cadastrarImovelRequest;
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
    public void deveRetornarErroAoNaoEncontrarUsuarioProprietario() throws UsuarioIdNaoExisteException {
        CadastrarImovelRequest cadastrarImovelRequest = buildCadastrarImovelRequest();

        when(usuarioService.buscarPeloId(cadastrarImovelRequest.getIdProprietario())).thenThrow(UsuarioIdNaoExisteException.class);

        assertThrows(UsuarioIdNaoExisteException.class, ()-> service.salvar(cadastrarImovelRequest));

    }

    @Test
    public void deveRetornarErroCamposObrigatoriosNaoPreenchidos() throws UsuarioIdNaoExisteException {
        CadastrarImovelRequest cadastrarImovelRequest = buildCadastrarImovelRequest();
        cadastrarImovelRequest.setTipoImovel(null);
        cadastrarImovelRequest.setEndereco(null);
        cadastrarImovelRequest.setIdentificacao(null);
        cadastrarImovelRequest.setIdProprietario(null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<CadastrarImovelRequest>> violations = validator.validate(cadastrarImovelRequest);

        assertEquals(4, violations.size());

    }

    @Test
    public void deveRetornarErroQuandoCEPEstiverForadoFormato() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        CadastrarImovelRequest cadastrarImovelRequest = buildCadastrarImovelRequest();
        cadastrarImovelRequest.getEndereco().setCep("29100100");
        final String mensagemEsperada = "O CEP deve ser informado no formato 99999-999.";

        Set<ConstraintViolation<CadastrarImovelRequest>> violations = validator.validate(cadastrarImovelRequest);
        ConstraintViolation cepConstraint = null;
        for (ConstraintViolation a : violations) {
            cepConstraint = a;
            break;
        }

        assertEquals(violations.size(), 1);
        assertEquals(mensagemEsperada, cepConstraint.getMessage());

    }

    @Test
    public void deveRetornarPaginaVazia() {
        Pageable pageable;
        pageable = PageRequest.of(0,10, Sort.by(Sort.Direction.ASC,"identificacao"));
        List<Imovel> imoveis = new ArrayList<>();
        Page<Imovel> expected = new PageImpl<>(imoveis);

        when(repository.findAllByAtivoIsTrue(pageable)).thenReturn(expected);

        assertEquals(0,service.buscarTodos(pageable).getTotalElements());
    }

    @Test
    public void deveRetornarPaginaOrdenada() {
        Pageable pageable = PageRequest.of(0,5,Sort.by(Sort.Direction.ASC,"identificacao"));
        List<Imovel> imoveis = new ArrayList<>();

        imoveis.add(buildImovel());
        Imovel imovel2 = buildImovel();
        imovel2.setIdentificacao("casa na praia");
        imoveis.add(imovel2);

        Page<Imovel> expected = new PageImpl<Imovel>(imoveis,pageable,imoveis.size());

        when(repository.findAllByAtivoIsTrue(pageable)).thenReturn(expected);
        Page<Imovel> recebidos = service.buscarTodos(pageable);

        assertEquals(2,recebidos.getTotalElements());
        assertEquals(1,recebidos.getTotalPages());
        assertEquals(5,recebidos.getSize());
        assertEquals(Sort.by(Sort.Direction.ASC,"identificacao"),recebidos.getSort());
        assertEquals(service.buscarTodos(pageable), expected);

    }

    @Test
    public void deveRetornarPageVaziaQuandoProprietarioNaoExistir() throws UsuarioIdNaoExisteException {
        Long id = 1L;
        Usuario proprietario = null;
        Pageable pageable = PageRequest.of(0,5,Sort.by(Sort.Direction.ASC,"identificacao"));
        List<Imovel> imoveis = new ArrayList<>();
        Page<Imovel> expected = new PageImpl<Imovel>(imoveis,pageable,imoveis.size());

        when(usuarioService.buscarPeloId(id)).thenThrow(UsuarioIdNaoExisteException.class);
        when(repository.findAllByProprietario(proprietario,pageable)).thenReturn(expected);

        assertTrue(service.buscarImoveisPorProprietario(pageable,id).isEmpty());
    }

    @Test
    public void deveRetornarPageComImoveisDoProprietario() throws UsuarioIdNaoExisteException {
        Long id = 1L;
        Usuario proprietario = new Usuario();
        Pageable pageable = PageRequest.of(0,5,Sort.by(Sort.Direction.ASC,"identificacao"));
        List<Imovel> imoveis = new ArrayList<>();
        imoveis.add(buildImovel());
        Imovel imovel2 = buildImovel();
        imovel2.setIdentificacao("casa na praia");
        imoveis.add(imovel2);

        Page<Imovel> expected = new PageImpl<Imovel>(imoveis,pageable,imoveis.size());

        when(usuarioService.buscarPeloId(id)).thenReturn(proprietario);
        when(repository.findAllByAtivoIsTrueAndProprietarioEquals(proprietario,pageable)).thenReturn(expected);

        assertEquals(2, service.buscarImoveisPorProprietario(pageable, id).getTotalElements());
    }

    @Test
    public void deveRetornarErroAoNaoEncontrarImovelPeloId(){
        Long id = 2L;
        String mensagemesperada = String.format("Nenhum(a) Imovel com Id com o valor '%s' foi encontrado.",id);
        when(repository.existsByIdAndAtivoIsTrue(id)).thenReturn(false);

        ImovelIdNaoExisteException e = assertThrows(ImovelIdNaoExisteException.class, ()->service.buscarPeloId(id));

        assertEquals(mensagemesperada, e.getMessage());
    }

    @Test
    public void deveRetornarImovelEncontradoPeloId() throws ImovelIdNaoExisteException {
        Long id = 1L;
        Imovel expected = buildImovel();

        when(repository.findById(id)).thenReturn(java.util.Optional.ofNullable(expected));
        when(repository.existsByIdAndAtivoIsTrue(id)).thenReturn(true);

        Imovel imovel = service.buscarPeloId(id);

        assertEquals(imovel,expected);

    }

    @Test
    public void deveRetornarErroAoExcluirImovelComAnuncioAtivo() throws ImovelIdNaoExisteException, ImovelPossuiAnuncioException {
        Long id = 1L;
        Imovel imovel = buildImovel();
        imovel.setId(id);
        String mensagemEsperada = "Não é possível excluir um imóvel que possua um anúncio.";

        when(repository.existsByIdAndAtivoIsTrue(id)).thenReturn(true);
        when(repository.findById(id)).thenReturn(java.util.Optional.ofNullable(imovel));
        when(verificaAnuncioPorImovelService.verificaSeImovelPossuiAnuncioAtivo(imovel)).thenReturn(true);

        ImovelPossuiAnuncioException e = assertThrows(ImovelPossuiAnuncioException.class, () -> service.excluir(id));

        assertEquals(mensagemEsperada, e.getMessage());
    }

    @Test
    public void deveExcluirImovel() throws ImovelIdNaoExisteException, ImovelPossuiAnuncioException {
        Long id = 1L;
        Imovel imovel = buildImovel();
        imovel.setId(id);

        when(repository.existsByIdAndAtivoIsTrue(id)).thenReturn(true);
        when(repository.findById(id)).thenReturn(java.util.Optional.ofNullable(imovel));
        when(verificaAnuncioPorImovelService.verificaSeImovelPossuiAnuncioAtivo(imovel)).thenReturn(false);

        service.excluir(imovel.getId());

        assertFalse(imovel.isAtivo());
    }





}





