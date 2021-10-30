package io.github.cwireset.tcc.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pagamento {

    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaEscolhida;

    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

}
