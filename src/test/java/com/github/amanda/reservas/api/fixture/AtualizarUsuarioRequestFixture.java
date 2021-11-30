package com.github.amanda.reservas.api.fixture;

import com.github.amanda.reservas.api.domain.Endereco;
import com.github.amanda.reservas.api.request.AtualizarUsuarioRequest;

import java.time.LocalDate;

public class AtualizarUsuarioRequestFixture {

    private AtualizarUsuarioRequest.AtualizarUsuarioRequestBuilder builder = AtualizarUsuarioRequest.builder();

    public static AtualizarUsuarioRequestFixture get() {
        return new AtualizarUsuarioRequestFixture();
    }

    public AtualizarUsuarioRequest build() {
        return builder.build();
    }

    public AtualizarUsuarioRequestFixture valido() {
        comNome()
        .comEmail()
        .comSenha()
        .comDataNascimento()
        .comEnderecoValido();

        return this;
    }

    public AtualizarUsuarioRequestFixture comNome() {
        builder.nome("Updated Tester Subject");
        return this;
    }

    public AtualizarUsuarioRequestFixture comEmail() {
        comEmail("updatedemail@teste.com");
        return this;
    }

    public AtualizarUsuarioRequestFixture comEmail(String email) {
        builder.email(email);
        return this;
    }

    public AtualizarUsuarioRequestFixture comSenha() {
        builder.senha("updateds1e2n3h4a5");
        return this;
    }

    public AtualizarUsuarioRequestFixture comDataNascimento() {
        builder.dataNascimento(LocalDate.of(1981, 1, 1));
        return this;
    }

    public AtualizarUsuarioRequestFixture comEnderecoValido() {
        comEndereco(EnderecoFixture.get().valido().build());
        return this;
    }

    public AtualizarUsuarioRequestFixture comEndereco(Endereco endereco) {
        builder.endereco(endereco);
        return this;
    }

}
