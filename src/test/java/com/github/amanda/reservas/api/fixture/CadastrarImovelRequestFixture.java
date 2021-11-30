package com.github.amanda.reservas.api.fixture;

import com.github.amanda.reservas.api.domain.Endereco;
import com.github.amanda.reservas.api.domain.TipoImovel;
import com.github.amanda.reservas.api.request.CadastrarImovelRequest;

import java.util.Arrays;

public class CadastrarImovelRequestFixture {

    private CadastrarImovelRequest.CadastrarImovelRequestBuilder builder = CadastrarImovelRequest.builder();

    public static CadastrarImovelRequestFixture get() {
        return new CadastrarImovelRequestFixture();
    }

    public CadastrarImovelRequest build() {
        return builder.build();
    }

    public CadastrarImovelRequestFixture semCamposObrigatorios() {
        return comCaracteristicas();
    }

    public CadastrarImovelRequestFixture valido() {
        return comTipoImovel()
                .comIdentificacao()
                .comIdProprietario()
                .comCaracteristicas()
                .comEnderecoValido();
    }

    public CadastrarImovelRequestFixture comTipoImovel() {
        return comTipoImovel(TipoImovel.CASA);
    }

    public CadastrarImovelRequestFixture comTipoImovel(TipoImovel tipoImovel) {
        builder.tipoImovel(tipoImovel);
        return this;
    }

    public CadastrarImovelRequestFixture comEndereco(Endereco endereco) {
        builder.endereco(endereco);
        return this;
    }

    public CadastrarImovelRequestFixture comEnderecoValido() {
        return comEndereco(EnderecoFixture.get().valido().build());
    }

    public CadastrarImovelRequestFixture comIdentificacao() {
        builder.identificacao("Casa da Fixture");
        return this;
    }

    public CadastrarImovelRequestFixture comIdProprietario() {
        return comIdProprietario(1L);
    }

    public CadastrarImovelRequestFixture comIdProprietario(Long idProprietario) {
        builder.idProprietario(idProprietario);
        return this;
    }

    public CadastrarImovelRequestFixture comCaracteristicas() {
        builder.caracteristicas(Arrays.asList(CaracteristicaImovelFixture.get().valido().build(), CaracteristicaImovelFixture.get().valido().build()));
        return this;
    }

}
