package io.github.cwireset.tcc.repository;

import io.github.cwireset.tcc.domain.Reserva;
import io.github.cwireset.tcc.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Reserva save(Reserva reserva);
    Page<Reserva> findAllBySolicitante(Usuario solicitante, Pageable pageable);
    Page<Reserva> findReservasBySolicitanteAndPeriodo_DataHoraInicialIsAfter(Usuario solicitante, LocalDateTime inicio, Pageable pageable);
    Page<Reserva> findReservasBySolicitanteAndPeriodo_DataHoraFinalIsBefore(Usuario solicitante, LocalDateTime fim, Pageable pageable);
    Page<Reserva> findReservasBySolicitanteAndPeriodo_DataHoraInicialIsAfterAndPeriodo_DataHoraFinalIsBefore(Usuario solicitante, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
    Page<Reserva> findReservasByAnuncio_Anunciante(Usuario anunciante, Pageable pageable);
    boolean existsById(Long idReserva);
    Optional<Reserva> findById(Long idReserva);
    List<Reserva> findAllByPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsLessThanEqualAndPeriodo_DataHoraFinalIsGreaterThanEqualOrPeriodo_DataHoraInicialIsGreaterThanAndPeriodo_DataHoraFinalIsLessThan(LocalDateTime dataHoraInicio,LocalDateTime dataHoraInicio1, LocalDateTime dataHoraFinal,LocalDateTime dataHoraFinal1, LocalDateTime dataHoraIncio2,LocalDateTime dataHoraFinal2);

}
