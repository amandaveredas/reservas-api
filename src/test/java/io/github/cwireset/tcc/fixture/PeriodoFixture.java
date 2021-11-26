package io.github.cwireset.tcc.fixture;

import io.github.cwireset.tcc.domain.Periodo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PeriodoFixture {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private Periodo.PeriodoBuilder builder = Periodo.builder();

    public static PeriodoFixture get() {
        return new PeriodoFixture();
    }

    public Periodo build() {
        return builder.build();
    }

    public PeriodoFixture valido(){
        comDataHoraInicial()
            .comDataHoraFinal();

        return this;
    }

    public PeriodoFixture comDataHoraInicial(){
        builder.dataHoraInicial(LocalDateTime.parse(LocalDateTime.now().format(formatter)));
        return this;
    }

    public PeriodoFixture comDataHoraFinal(){
        builder.dataHoraFinal(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(formatter)));
        return this;
    }

    public PeriodoFixture comDataHoraFinal(LocalDateTime dataHoraFinal){
        builder.dataHoraFinal(dataHoraFinal);
        return this;
    }

    public PeriodoFixture comDataHoraInicial(LocalDateTime dataHoraInicial){
        builder.dataHoraInicial(dataHoraInicial);
        return this;
    }

}
