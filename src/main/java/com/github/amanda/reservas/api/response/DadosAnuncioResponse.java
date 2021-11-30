package com.github.amanda.reservas.api.response;

import com.github.amanda.reservas.api.domain.Imovel;
import com.github.amanda.reservas.api.domain.Usuario;
import com.github.amanda.reservas.api.domain.FormaPagamento;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DadosAnuncioResponse {

    private Long id;
    private Imovel imovel;
    private Usuario anunciante;
    private List<FormaPagamento> formasAceitas;
    private String descricao;

}
