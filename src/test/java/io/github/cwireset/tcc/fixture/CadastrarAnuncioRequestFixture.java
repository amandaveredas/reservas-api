package io.github.cwireset.tcc.fixture;

import io.github.cwireset.tcc.domain.FormaPagamento;
import io.github.cwireset.tcc.domain.TipoAnuncio;
import io.github.cwireset.tcc.request.CadastrarAnuncioRequest;

import java.math.BigDecimal;
import java.util.Arrays;

public class CadastrarAnuncioRequestFixture {

    private static final long ID_ANUNCIANTE_EXISTENTE = 1L;
    private static final long ID_IMOVEL_SEM_ANUNCIO = 11L;

    private CadastrarAnuncioRequest.CadastrarAnuncioRequestBuilder builder = CadastrarAnuncioRequest.builder();

    public static CadastrarAnuncioRequestFixture get() {
        return new CadastrarAnuncioRequestFixture();
    }

    public CadastrarAnuncioRequest build() {
        return builder.build();
    }

    public CadastrarAnuncioRequestFixture valido() {
        comIdImovel()
            .comIdAnunciante()
            .comValorDiaria()
            .comFormasDePagamento()
            .comDescricao()
            .comTipoAnuncio();

            return this;
    }

    public CadastrarAnuncioRequestFixture comIdImovel(){
        builder.idImovel(ID_IMOVEL_SEM_ANUNCIO);
        return this;
    }

    public CadastrarAnuncioRequestFixture comIdAnunciante(){
        builder.idAnunciante(ID_ANUNCIANTE_EXISTENTE);
        return this;
    }

    public CadastrarAnuncioRequestFixture comValorDiaria(){
        builder.valorDiaria(BigDecimal.valueOf(200));
        return this;
    }

    public CadastrarAnuncioRequestFixture comFormasDePagamento(){
        builder.formasAceitas(Arrays.asList(FormaPagamento.PIX, FormaPagamento.DINHEIRO));
        return this;
    }

    public CadastrarAnuncioRequestFixture comDescricao(){
        builder.descricao("Casa perfeita para passar a virada do ano");
        return this;
    }

    public CadastrarAnuncioRequestFixture comTipoAnuncio(){
        builder.tipoAnuncio(TipoAnuncio.COMPLETO);
        return this;
    }

    public CadastrarAnuncioRequestFixture comIdAnunciante(long idAnunciante){
        builder.idAnunciante(idAnunciante);
        return this;
    }

    public CadastrarAnuncioRequestFixture comIdImovel(long idImovel){
        builder.idImovel(idImovel);
        return this;
    }

}
