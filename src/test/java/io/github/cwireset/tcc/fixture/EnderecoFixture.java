package io.github.cwireset.tcc.fixture;

import io.github.cwireset.tcc.domain.Endereco;

public class EnderecoFixture {

    private Endereco.EnderecoBuilder builder = Endereco.builder();

    public static EnderecoFixture get() {
        return new EnderecoFixture();
    }

    public Endereco build() {
        return builder.build();
    }

    public EnderecoFixture valido() {
        comCep()
        .comCidade()
        .comEstado()
        .comBairro()
        .comLogradouro()
        .comNumero()
        .comComplemento();

        return this;
    }

    public EnderecoFixture comCep() {
        comCep("96015-180");
        return this;
    }

    public EnderecoFixture comCep(String cep) {
        builder.cep(cep);
        return this;
    }

    public EnderecoFixture comCidade() {
        builder.cidade("Cidade Fixture");
        return this;
    }

    public EnderecoFixture comEstado() {
        builder.estado("FF");
        return this;
    }

    public EnderecoFixture comBairro() {
        builder.bairro("Bairro Fixture");
        return this;
    }

    public EnderecoFixture comLogradouro() {
        builder.logradouro("Rua dos Bobos");
        return this;
    }

    public EnderecoFixture comNumero() {
        builder.numero("0");
        return this;
    }

    public EnderecoFixture comComplemento() {
        builder.complemento("Complemento Fixture");
        return this;
    }

}
