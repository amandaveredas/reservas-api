package com.github.amanda.reservas.api.service;

import com.github.amanda.reservas.api.domain.Endereco;
import com.github.amanda.reservas.api.domain.Usuario;
import com.github.amanda.reservas.api.exception.CpfJaExisteException;
import com.github.amanda.reservas.api.exception.EmailJaExisteException;
import com.github.amanda.reservas.api.exception.UsuarioCpfNaoExisteException;
import com.github.amanda.reservas.api.exception.UsuarioIdNaoExisteException;
import com.github.amanda.reservas.api.repository.UsuarioRepository;
import com.github.amanda.reservas.api.request.AtualizarUsuarioRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService service;

    @Mock
    private UsuarioRepository repository;

    @Mock
    PageImpl page;

    @BeforeEach
    void setUp(){


    }

    private Usuario buildUsuario(){
        final String nome = "Usuario Teste";
        final String email = "usuario@teste.com";
        final String senha = "teste123";
        final String cpf = "12312312312";
        final LocalDate dataNascimento = LocalDate.of(1989, 11,06);

        final String cep = "29290-290";
        final String logradouro = "Rua do Jacó";
        final String numero = "200";
        final String complemento = "10";
        final String bairro = "Alamdeda dos anjos";
        final String cidade = "Roliúdi";
        final String estado = "ES";
        final Long id = 1L;

        final Endereco endereco = Endereco.builder()
                .cep(cep)
                .logradouro(logradouro)
                .numero(numero)
                .complemento(complemento)
                .bairro(bairro)
                .cidade(cidade)
                .estado(estado).build();

        final Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senha(senha)
                .cpf(cpf)
                .dataNascimento(dataNascimento)
                .endereco(endereco)
                .build();

        return usuario;
    }

    private AtualizarUsuarioRequest buildUsuarioRequest(){
        final String nome = "William Shakespear";
        final String email = "shake@teste.com";
        final String senha = "shake123";
        final LocalDate dataNascimento = LocalDate.of(1880, 12,10);

        final String cep = "30300-290";
        final String logradouro = "Rua do Shake";
        final String numero = "100";
        final String complemento = "100";
        final String bairro = "Alamdeda dos shakes";
        final String cidade = "Paris";
        final String estado = "MG";

        final Endereco endereco = Endereco.builder()
                .cep(cep)
                .logradouro(logradouro)
                .numero(numero)
                .complemento(complemento)
                .bairro(bairro)
                .cidade(cidade)
                .estado(estado).build();

        final AtualizarUsuarioRequest usuarioRequest = AtualizarUsuarioRequest.builder()
                .nome(nome)
                .email(email)
                .senha(senha)
                .dataNascimento(dataNascimento)
                .endereco(endereco)
                .build();

        return usuarioRequest;
    }

    //salvar

    @Test
    public void deveRetornarErroQuandoJaExisteUsuarioComMesmoEmail(){
        final Usuario usuario = buildUsuario();
        final String mensagemEsperada = String.format("Já existe um recurso do tipo Usuario com E-Mail com o valor '%s'.",usuario.getEmail());

        when(repository.existsByEmail(usuario.getEmail())).thenReturn(true);
        EmailJaExisteException e = assertThrows(EmailJaExisteException.class, () -> service.salvar(usuario));

        assertEquals(mensagemEsperada,e.getMessage());

    }

    @Test
    public void deveRetornarErroQuandoJaExisteUsuarioComMesmoCpf(){
        final Usuario usuario = buildUsuario();
        final String mensagemEsperada = String.format("Já existe um recurso do tipo Usuario com CPF com o valor '%s'.",usuario.getCpf());

        when(repository.existsByCpf(usuario.getCpf())).thenReturn(true);
        CpfJaExisteException e = assertThrows(CpfJaExisteException.class, () -> service.salvar(usuario));

        assertEquals(mensagemEsperada,e.getMessage());

    }

    @Test
    public void deveRetornarErroCamposObrigatoriosNaoPreenchidos() {
        Usuario usuario = buildUsuario();
        usuario.setNome(null);
        usuario.setEmail(null);
        usuario.setSenha(null);
        usuario.setDataNascimento(null);
        usuario.setCpf(null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);

        assertEquals(5, violations.size());

    }

    @Test
    public void deveRetornarErroQuandoCEPEstiverForadoFormato() throws EmailJaExisteException, CpfJaExisteException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        final Usuario usuario = buildUsuario();
        usuario.getEndereco().setCep("29100100");
        final String mensagemEsperada = "O CEP deve ser informado no formato 99999-999.";

        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        ConstraintViolation cepConstraint = null;
        for(ConstraintViolation a:violations){
            cepConstraint = a;
            break;
        }

        assertEquals(violations.size(),1);
        assertEquals(mensagemEsperada,cepConstraint.getMessage());
    }

    @Test
    public void deveRetornarErroQuandoCPFEstiverForadoFormato() throws EmailJaExisteException, CpfJaExisteException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        final Usuario usuario = buildUsuario();
        usuario.setCpf("000.000.000-00");
        final String mensagemEsperada = "O CPF deve ser informado no formato 99999999999.";

        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        ConstraintViolation cpfConstraint = null;
        for(ConstraintViolation a:violations){
            cpfConstraint = a;
            break;
        }

        assertEquals(violations.size(),1);
        assertEquals(mensagemEsperada,cpfConstraint.getMessage());
    }

    @Test
    public void deveCriarUsuario() throws EmailJaExisteException, CpfJaExisteException {
        final Usuario usuario = buildUsuario();
        final Usuario expected = Usuario.builder()
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .senha(usuario.getSenha())
                .cpf(usuario.getCpf())
                .dataNascimento(usuario.getDataNascimento())
                .endereco(usuario.getEndereco())
                .build();

        when(repository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(repository.existsByCpf(usuario.getCpf())).thenReturn(false);
        when(repository.save(usuario)).thenReturn(expected);

        service.salvar(usuario);

        assertEquals(expected,usuario);

    }

    //buscarPeloId
    @Test
    public void deveRetornarErroQuandoOIdNaoExistir(){
        final Long id = 10L;
        final String mensagemEsperada = String.format("Nenhum(a) Usuario com Id com o valor '%s' foi encontrado.",id);

        when(repository.existsById(id)).thenReturn(false);

        UsuarioIdNaoExisteException e = assertThrows(UsuarioIdNaoExisteException.class, () -> service.buscarPeloId(id));
        assertEquals(mensagemEsperada,e.getMessage());

    }

    @Test
    public void deveRetornarUsuarioEncontradoPeloId() throws UsuarioIdNaoExisteException {
        final Long id =1L;
        Optional<Usuario> expected = Optional.ofNullable(buildUsuario());
        expected.get().setId(1L);

        when(repository.existsById(id)).thenReturn(true);
        when(repository.findById(id)).thenReturn(expected);
        service.buscarPeloId(id);

        assertEquals(id, expected.get().getId());
    }

    //buscarPeloCPF
    @Test
    public void deveRetornarErroQuandoOCpfNaoExistir(){
        Usuario usuario = buildUsuario();
        final String mensagemEsperada = String.format("Nenhum(a) Usuario com CPF com o valor '%s' foi encontrado.",usuario.getCpf());

        when(repository.existsByCpf(usuario.getCpf())).thenReturn(false);

        UsuarioCpfNaoExisteException e = assertThrows(UsuarioCpfNaoExisteException.class, () -> service.buscaPeloCpf(usuario.getCpf()));
        assertEquals(mensagemEsperada,e.getMessage());

    }

    @Test
    public void deveRetornarUsuarioEncontradoPeloCpf() throws UsuarioCpfNaoExisteException {
        final String cpf = "11111111111";
        Usuario expected = buildUsuario();
        expected.setCpf(cpf);

        when(repository.existsByCpf(cpf)).thenReturn(true);
        when(repository.findByCpf(cpf)).thenReturn(expected);
        service.buscaPeloCpf(cpf);

        assertEquals(cpf, expected.getCpf());
    }

    //atualizar
    @Test
    public void deveRetornarErroAoNaoEncontrarUsuarioASerAtualizado(){
        Long id =10L;
        AtualizarUsuarioRequest usuarioRequest = buildUsuarioRequest();
        final String mensagemEsperada = String.format("Nenhum(a) Usuario com Id com o valor '%s' foi encontrado.",id);

        when(repository.existsById(id)).thenReturn(false);

        UsuarioIdNaoExisteException e = assertThrows(UsuarioIdNaoExisteException.class, () -> service.atualizar(id, usuarioRequest));
        assertEquals(mensagemEsperada,e.getMessage());
    }

    @Test
    public void deveRetornarErroQuandoJaExisteUsuarioComMesmoEmailAoSerAtualizado(){
        Long idASerAtualizado =1L;
        Long idUsuarioQuePossuiOEmail = 2L;

        Usuario usuarioQuePossuiOEmail = buildUsuario();
        usuarioQuePossuiOEmail.setId(idUsuarioQuePossuiOEmail);
        //usuario@teste.com

        Usuario usuarioQueQueroAtualizar = buildUsuario();
        usuarioQueQueroAtualizar.setId(idASerAtualizado);
        usuarioQueQueroAtualizar.setEmail("amanda@amanda.com");

        AtualizarUsuarioRequest usuarioRequest = buildUsuarioRequest();
        usuarioRequest.setEmail(usuarioQuePossuiOEmail.getEmail());
        //usurio@teste.com

        final String mensagemEsperada = String.format("Já existe um recurso do tipo Usuario com E-Mail com o valor '%s'.",usuarioRequest.getEmail());

        when(repository.existsById(idASerAtualizado)).thenReturn(true);
        when(repository.existsByEmail(usuarioRequest.getEmail())).thenReturn(true);
        when(repository.findById(idASerAtualizado)).thenReturn(Optional.of(usuarioQueQueroAtualizar));

        EmailJaExisteException e = assertThrows(EmailJaExisteException.class, () -> service.atualizar(idASerAtualizado, usuarioRequest));

        assertEquals(mensagemEsperada,e.getMessage());
    }

    @Test
    public void deveAtualizarUsuarioMantendoEmail() throws UsuarioIdNaoExisteException, EmailJaExisteException {
        Long idASerAtualizado =1L;
        Long idUsuarioQuePossuiOEmail = 1L;

        final AtualizarUsuarioRequest usuarioRequest = buildUsuarioRequest();

        Usuario usuarioQueQueroAtualizar = buildUsuario();
        usuarioQueQueroAtualizar.setId(idASerAtualizado);
        usuarioQueQueroAtualizar.setEmail(usuarioRequest.getEmail());

        Usuario usuarioQuePossuiOEmail = buildUsuario();
        usuarioQuePossuiOEmail.setId(idUsuarioQuePossuiOEmail);
        usuarioQuePossuiOEmail.setEmail(usuarioRequest.getEmail());

        final Usuario expected = buildUsuario();
        expected.setId(idASerAtualizado);
        expected.setNome(usuarioRequest.getNome());
        expected.setSenha(usuarioRequest.getSenha());
        expected.setEndereco(usuarioRequest.getEndereco());
        expected.setDataNascimento(usuarioRequest.getDataNascimento());
        expected.setEmail(usuarioRequest.getEmail());

        when(repository.existsById(idASerAtualizado)).thenReturn(true);
        when(repository.existsByEmail(usuarioRequest.getEmail())).thenReturn(true);
        when(repository.findById(idASerAtualizado)).thenReturn(Optional.of(usuarioQueQueroAtualizar));

        service.atualizar(idASerAtualizado,usuarioRequest);

        assertEquals(usuarioQueQueroAtualizar,expected);
    }

    @Test public void deveAtualizarComEmailQueNaoExisteNoBanco() throws UsuarioIdNaoExisteException, EmailJaExisteException {
        Long idASerAtualizado =1L;

        Usuario usuarioQueQueroAtualizar = buildUsuario();
        usuarioQueQueroAtualizar.setId(idASerAtualizado);

        final AtualizarUsuarioRequest usuarioRequest = buildUsuarioRequest();

        final Usuario expected = buildUsuario();
        expected.setId(idASerAtualizado);
        expected.setNome(usuarioRequest.getNome());
        expected.setSenha(usuarioRequest.getSenha());
        expected.setEndereco(usuarioRequest.getEndereco());
        expected.setDataNascimento(usuarioRequest.getDataNascimento());
        expected.setEmail(usuarioRequest.getEmail());

        when(repository.existsById(idASerAtualizado)).thenReturn(true);
        when(repository.existsByEmail(usuarioRequest.getEmail())).thenReturn(false);
        when(repository.findById(idASerAtualizado)).thenReturn(Optional.of(usuarioQueQueroAtualizar));

        service.atualizar(idASerAtualizado,usuarioRequest);

        assertEquals(usuarioQueQueroAtualizar,expected);

    }

    @Test
    public void deveRetornarPaginaVazia() {
       Pageable pageable;
       pageable = PageRequest.of(0,10,Sort.by(Sort.Direction.ASC,"name"));
       ArgumentCaptor<Pageable> argumentCaptor = ArgumentCaptor.forClass(Pageable.class);
       List<Usuario> usuarios = new ArrayList<>();
       Page<Usuario> expected = new PageImpl<>(usuarios);

       when(repository.findAll(argumentCaptor.capture())).thenReturn(expected);

       assertEquals(0,service.buscarTodos(pageable).getTotalElements());
       assertEquals(pageable, argumentCaptor.getValue());
    }

    @Test
    public void deveRetornarPaginaOrdenada() {
        Pageable pageable = PageRequest.of(0,10,Sort.by(Sort.Direction.ASC,"nome"));
        List<Usuario> usuarios = new ArrayList<>();
        ArgumentCaptor<Pageable> argumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        Usuario usuario1 = buildUsuario();
        usuario1.setNome("Valquiria");
        usuarios.add(usuario1);
        Usuario usuario2 = buildUsuario();
        usuario2.setNome("Alessandra");
        usuarios.add(usuario2);
        Usuario usuario3 = buildUsuario();
        usuario3.setNome("Rafael");
        usuarios.add(usuario3);
        Usuario usuario4 = buildUsuario();
        usuario4.setNome("Janaina");
        usuarios.add(usuario4);
        Usuario usuario5 = buildUsuario();
        usuario5.setNome("Karina");
        usuarios.add(usuario5);
        Usuario usuario6 = buildUsuario();
        usuario6.setNome("Bárbara");
        usuarios.add(usuario6);
        Usuario usuario7 = buildUsuario();
        usuario7.setNome("Roni");
        usuarios.add(usuario7);
        Usuario usuario8 = buildUsuario();
        usuario8.setNome("Horus");
        usuarios.add(usuario8);
        Usuario usuario9 = buildUsuario();
        usuario9.setNome("Marluce");
        usuarios.add(usuario9);
        Usuario usuario10 = buildUsuario();
        usuario10.setNome("Djalma");
        usuarios.add(usuario10);
        Usuario usuario11 = buildUsuario();
        usuario11.setNome("Eliane");
        usuarios.add(usuario11);

        Page<Usuario> expected = new PageImpl<Usuario>(usuarios,pageable,usuarios.size());

        when(repository.findAll(argumentCaptor.capture())).thenReturn(expected);
        service.buscarTodos(pageable);

        assertEquals(11,expected.getTotalElements());
        assertEquals(2,expected.getTotalPages());
        assertEquals(10,expected.getSize());
        assertEquals(Sort.by(Sort.Direction.ASC,"nome"),expected.getSort());
        assertEquals(pageable, argumentCaptor.getValue());

    }









}
