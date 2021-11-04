package io.github.cwireset.tcc.response;

import io.github.cwireset.tcc.domain.Pagamento;
import io.github.cwireset.tcc.domain.Periodo;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class InformacaoReservaResponse {

    private Long idReserva;

    private DadosSolicitanteResponse solicitante;

    private Integer quantidadePessoas;

    private DadosAnuncioResponse anuncio;

    private Periodo periodo;

    private Pagamento pagamento;

}
