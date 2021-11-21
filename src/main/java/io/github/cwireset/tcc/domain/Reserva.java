package io.github.cwireset.tcc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_solicitante")
    private Usuario solicitante;

    @ManyToOne
    @JoinColumn(name = "id_anuncio")
    private Anuncio anuncio;

    @Embedded
    private Periodo periodo;

    private Integer quantidadePessoas;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataHoraReserva;

    @Embedded
    private Pagamento pagamento;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(id, reserva.id) && Objects.equals(solicitante, reserva.solicitante) && Objects.equals(anuncio, reserva.anuncio) && Objects.equals(periodo, reserva.periodo) && Objects.equals(quantidadePessoas, reserva.quantidadePessoas) && Objects.equals(pagamento, reserva.pagamento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, solicitante, anuncio, periodo, quantidadePessoas, pagamento);
    }
}
