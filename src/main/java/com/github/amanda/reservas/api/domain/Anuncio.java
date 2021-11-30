package com.github.amanda.reservas.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoAnuncio tipoAnuncio;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_imovel")
    private Imovel imovel;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_anunciante")
    private Usuario anunciante;

    private BigDecimal valorDiaria;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<FormaPagamento> formasAceitas;

    private String descricao;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean ativo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Anuncio anuncio = (Anuncio) o;
        return ativo == anuncio.ativo && Objects.equals(id, anuncio.id) && tipoAnuncio == anuncio.tipoAnuncio && Objects.equals(imovel, anuncio.imovel) && Objects.equals(anunciante, anuncio.anunciante) && Objects.equals(valorDiaria, anuncio.valorDiaria) && Objects.equals(formasAceitas, anuncio.formasAceitas) && Objects.equals(descricao, anuncio.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tipoAnuncio, imovel, anunciante, valorDiaria, formasAceitas, descricao, ativo);
    }
}
