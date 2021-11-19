package io.github.cwireset.tcc.request;

import io.github.cwireset.tcc.domain.CaracteristicaImovel;
import io.github.cwireset.tcc.domain.Endereco;
import io.github.cwireset.tcc.domain.TipoImovel;
import io.github.cwireset.tcc.domain.Usuario;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CadastrarImovelRequest {

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoImovel tipoImovel;

    @NotNull
    @Valid
    private Endereco endereco;

    @NotBlank
    private String identificacao;

    @NotNull
    private Long idProprietario;

    private List<CaracteristicaImovel> caracteristicas;
}
