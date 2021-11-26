package io.github.cwireset.tcc.fixture;

import io.github.cwireset.tcc.domain.Endereco;
import io.github.cwireset.tcc.domain.Usuario;

import java.time.LocalDate;

public class UsuarioFixture {

    private Usuario.UsuarioBuilder builder = Usuario.builder();

    public static UsuarioFixture get() {
        return new UsuarioFixture();
    }

    public UsuarioFixture valido() {
        comCpf()
        .comNome()
        .comEmail()
        .comSenha()
        .comDataNascimento()
        .comEnderecoValido();

        return this;
    }

    public UsuarioFixture semCamposObrigatorios() {
        comEnderecoValido();

        return this;
    }

    public UsuarioFixture comCpf() {
        comCpf("11122233344");
        return this;
    }

    public UsuarioFixture comCpf(String cpf) {
        builder.cpf(cpf);
        return this;
    }

    public UsuarioFixture comNome() {
        builder.nome("Tester Subject");
        return this;
    }

    public UsuarioFixture comEmail() {
        comEmail("email@teste.com");
        return this;
    }

    public UsuarioFixture comEmail(String email) {
        builder.email(email);
        return this;
    }

    public UsuarioFixture comSenha() {
        builder.senha("s1e2n3h4a5");
        return this;
    }

    public UsuarioFixture comDataNascimento() {
        builder.dataNascimento(LocalDate.of(1980, 1, 1));
        return this;
    }

    public UsuarioFixture comEnderecoValido() {
        comEndereco(EnderecoFixture.get().valido().build());
        return this;
    }

    public UsuarioFixture comEndereco(Endereco endereco) {
        builder.endereco(endereco);
        return this;
    }

    public Usuario build() {
        return builder.build();
    }

}
