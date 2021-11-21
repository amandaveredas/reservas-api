package io.github.cwireset.tcc.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pagamento pagamento = (Pagamento) o;
        return Objects.equals(valorTotal, pagamento.valorTotal) && formaEscolhida == pagamento.formaEscolhida && status == pagamento.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valorTotal, formaEscolhida, status);
    }
}
