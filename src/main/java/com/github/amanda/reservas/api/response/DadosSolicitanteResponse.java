package com.github.amanda.reservas.api.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DadosSolicitanteResponse {

    private Long id;
    private String nome;
}
