package io.github.cwireset.tcc.request;

import io.github.cwireset.tcc.domain.Periodo;
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
