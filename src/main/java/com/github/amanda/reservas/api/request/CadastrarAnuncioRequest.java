package com.github.amanda.reservas.api.request;

import com.github.amanda.reservas.api.domain.FormaPagamento;
import com.github.amanda.reservas.api.domain.TipoAnuncio;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CadastrarAnuncioRequest {

    @NotNull
    private Long idImovel;

    @NotNull
    private Long idAnunciante;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoAnuncio tipoAnuncio;

    @NotNull
    private BigDecimal valorDiaria;

    @NotNull
    @Enumerated(EnumType.STRING)
    private List<FormaPagamento> formasAceitas;

    @NotBlank
    private String descricao;


}
