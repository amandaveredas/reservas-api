package io.github.cwireset.tcc.fixture;

import io.github.cwireset.tcc.domain.CaracteristicaImovel;

public class CaracteristicaImovelFixture {

    private CaracteristicaImovel.CaracteristicaImovelBuilder builder = CaracteristicaImovel.builder();

    public static CaracteristicaImovelFixture get() {
        return new CaracteristicaImovelFixture();
    }

    public CaracteristicaImovel build() {
        return builder.build();
    }

    public CaracteristicaImovelFixture valido() {
        comDescricao();

        return this;
    }

    public CaracteristicaImovelFixture comDescricao() {
        builder.descricao("Característica Fixture");
        return this;
    }

}
