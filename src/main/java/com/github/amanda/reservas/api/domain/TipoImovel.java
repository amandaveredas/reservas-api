package com.github.amanda.reservas.api.domain;

public enum TipoImovel {

    APARTAMENTO ("Apartamento"),
    CASA ("Casa"),
    HOTEL ("Hotel"),
    POUSADA("Pousada");

    private String descricao;

    TipoImovel(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
