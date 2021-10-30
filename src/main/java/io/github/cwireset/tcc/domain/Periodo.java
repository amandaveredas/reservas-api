package io.github.cwireset.tcc.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
public class Periodo {

    private LocalDateTime dataHoraInicial;

    private LocalDateTime dataHoraFinal;

}
