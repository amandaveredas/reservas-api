package com.github.amanda.reservas.api.request;

import com.github.amanda.reservas.api.domain.CaracteristicaImovel;
import com.github.amanda.reservas.api.domain.Endereco;
import com.github.amanda.reservas.api.domain.TipoImovel;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
