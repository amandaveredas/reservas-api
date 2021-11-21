package io.github.cwireset.tcc.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Imovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identificacao;

    @Enumerated(EnumType.STRING)
    private TipoImovel tipoImovel;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    @ManyToOne
    @JoinColumn(name = "id_proprietario")
    private Usuario proprietario;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_imovel")
    private List<CaracteristicaImovel> caracteristicas;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean ativo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Imovel imovel = (Imovel) o;
        return ativo == imovel.ativo && Objects.equals(id, imovel.id) && Objects.equals(identificacao, imovel.identificacao) && tipoImovel == imovel.tipoImovel && Objects.equals(endereco, imovel.endereco) && Objects.equals(proprietario, imovel.proprietario) && Objects.equals(caracteristicas, imovel.caracteristicas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, identificacao, tipoImovel, endereco, proprietario, caracteristicas, ativo);
    }
}
