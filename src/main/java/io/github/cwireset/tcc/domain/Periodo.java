package io.github.cwireset.tcc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Periodo {

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataHoraInicial;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataHoraFinal;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Periodo periodo = (Periodo) o;
        return Objects.equals(dataHoraInicial, periodo.dataHoraInicial) && Objects.equals(dataHoraFinal, periodo.dataHoraFinal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataHoraInicial, dataHoraFinal);
    }
}
