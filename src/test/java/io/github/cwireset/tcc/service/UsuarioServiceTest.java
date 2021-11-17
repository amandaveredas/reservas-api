package io.github.cwireset.tcc.service;

import io.github.cwireset.tcc.domain.Endereco;
import io.github.cwireset.tcc.domain.Usuario;
import io.github.cwireset.tcc.exception.CpfJaExisteException;
import io.github.cwireset.tcc.exception.EmailJaExisteException;
import io.github.cwireset.tcc.exception.UsuarioCpfNaoExisteException;
import io.github.cwireset.tcc.exception.UsuarioIdNaoExisteException;
import io.github.cwireset.tcc.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService service;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private ImagemService imagemService;

    private Pageable pageable;
    private Page<Usuario> page;


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

    @Test
    public void deveRetornarErroQuandoOCpfNaoExistir(){
        final String cpf = "11111111111";
        final String mensagemEsperada = String.format("Nenhum(a) Usuario com CPF com o valor '%s' foi encontrado.",cpf);

        when(repository.existsByCpf(cpf)).thenReturn(false);

        UsuarioCpfNaoExisteException e = assertThrows(UsuarioCpfNaoExisteException.class, () -> service.buscaPeloCpf(cpf));
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


}
