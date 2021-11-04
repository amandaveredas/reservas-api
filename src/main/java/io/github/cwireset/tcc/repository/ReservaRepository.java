package io.github.cwireset.tcc.repository;

import io.github.cwireset.tcc.domain.Periodo;
import io.github.cwireset.tcc.domain.Reserva;
import io.github.cwireset.tcc.domain.StatusPagamento;
import io.github.cwireset.tcc.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findAllByPeriodo_DataHoraInicialIsAfterOrPeriodo_DataHoraFinalIsBefore(LocalDateTime dataHoraInicio, LocalDateTime dataHoraFinal);
    Reserva save(Reserva reserva);
    Page<Reserva> findAllBySolicitante(Usuario solicitante, Pageable pageable);
    Page<Reserva> findReservasBySolicitanteAndPeriodo_DataHoraInicialIsAfter(Usuario solicitante, LocalDateTime inicio, Pageable pageable);
    Page<Reserva> findReservasBySolicitanteAndPeriodo_DataHoraFinalIsBefore(Usuario solicitante, LocalDateTime fim, Pageable pageable);
    Page<Reserva> findReservasBySolicitanteAndPeriodo_DataHoraInicialIsAfterAndPeriodo_DataHoraFinalIsBefore(Usuario solicitante, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
}
