package io.github.cwireset.tcc.repository;

import io.github.cwireset.tcc.domain.Reserva;
import io.github.cwireset.tcc.domain.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findAllByPeriodo_DataHoraInicialIsAfterOrPeriodo_DataHoraFinalIsBefore(LocalDateTime dataHoraInicio, LocalDateTime dataHoraFinal);
    Reserva save(Reserva reserva);

}
