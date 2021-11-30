package com.github.amanda.reservas.api.request;

import com.github.amanda.reservas.api.domain.Periodo;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CadastrarReservaRequest {

    @NotNull
    private Long idSolicitante;
    @NotNull
    private Long idAnuncio;
    @NotNull
    private Periodo periodo;
    @NotNull
    private Integer quantidadePessoas;
}
