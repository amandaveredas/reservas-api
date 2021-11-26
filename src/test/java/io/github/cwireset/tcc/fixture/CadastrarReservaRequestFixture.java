package io.github.cwireset.tcc.fixture;

import io.github.cwireset.tcc.domain.Periodo;
import io.github.cwireset.tcc.request.CadastrarReservaRequest;

import java.time.LocalDateTime;

public class CadastrarReservaRequestFixture {

    private static final long ID_SOLICITANTE_VALIDO = 6;
    private static final long ID_ANUNCIO_VALIDO = 8;

    private CadastrarReservaRequest.CadastrarReservaRequestBuilder builder = CadastrarReservaRequest.builder();

    public static CadastrarReservaRequestFixture get() {
        return new CadastrarReservaRequestFixture();
    }

    public CadastrarReservaRequest build() {
        return builder.build();
    }

    public CadastrarReservaRequestFixture valido(){
        comIdAnuncio()
        .comIdSolicitante()
        .comPeriodo()
        .comQuantidadePessoas();

        return this;
    }

    public CadastrarReservaRequestFixture comIdSolicitante() {
        builder.idSolicitante(ID_SOLICITANTE_VALIDO);
        return this;
    }

    public CadastrarReservaRequestFixture comIdAnuncio() {
        builder.idAnuncio(ID_ANUNCIO_VALIDO);
        return this;
    }

    public CadastrarReservaRequestFixture comQuantidadePessoas() {
        builder.quantidadePessoas(4);
        return this;
    }

    public CadastrarReservaRequestFixture comPeriodo(){
        builder.periodo(PeriodoFixture.get().valido().build());
        return this;
    }

    public CadastrarReservaRequestFixture comPeriodo(Periodo periodo){
        builder.periodo(periodo);
        return this;
    }

    public CadastrarReservaRequestFixture comDataHoraFinal(LocalDateTime data) {
        Periodo periodo = PeriodoFixture.get().valido().comDataHoraFinal(data).build();
        builder.periodo(periodo);

        return this;
    }

    public CadastrarReservaRequestFixture comIdAnunciante(long idAnuncio) {
        builder.idAnuncio(idAnuncio);

        return this;
    }

    public CadastrarReservaRequestFixture comIdSolicitante(long idSolicitante) {
        builder.idSolicitante(idSolicitante);

        return this;
    }

    public CadastrarReservaRequestFixture comQuantidadePessoas(int quantidadePessoas) {
        builder.quantidadePessoas(quantidadePessoas);

        return this;
    }
}
