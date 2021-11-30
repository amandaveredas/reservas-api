package com.github.amanda.reservas.api.response;

import com.github.amanda.reservas.api.domain.Pagamento;
import com.github.amanda.reservas.api.domain.Periodo;
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
