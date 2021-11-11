package io.github.cwireset.tcc.service.reserva;

import io.github.cwireset.tcc.domain.FormaPagamento;
import io.github.cwireset.tcc.domain.Reserva;
import io.github.cwireset.tcc.domain.StatusPagamento;
import io.github.cwireset.tcc.exception.FormaPagaMentoInvalidaException;
import io.github.cwireset.tcc.exception.ReservaNaoExisteException;
import io.github.cwireset.tcc.exception.ReservaNaoPagaException;
import io.github.cwireset.tcc.exception.ReservaNaoPendenteException;
import io.github.cwireset.tcc.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagamentoReservaService {

    private ReservaRepository repository;

    @Autowired
    public PagamentoReservaService(ReservaRepository repository) {
        this.repository = repository;
    }

    public void pagar(Long idReserva, Enum<FormaPagamento> formaPagamento) throws ReservaNaoExisteException, FormaPagaMentoInvalidaException, ReservaNaoPendenteException {
        Reserva reserva = verificaSeExisteERetornaAReserva(idReserva);
        List<FormaPagamento> formasAceitas = reserva.getAnuncio().getFormasAceitas();

        if (!formasAceitas.contains(formaPagamento)){
            String aceitas = "";
            for(FormaPagamento f: formasAceitas){
                aceitas+= f+", ";
            }
            aceitas = aceitas.substring(0,aceitas.length()-2);
            throw new FormaPagaMentoInvalidaException(formaPagamento,aceitas);
        }

        if(!verificaSeReservaPendente(reserva)){
            throw new ReservaNaoPendenteException( "Não é possível realizar o pagamento para esta reserva, pois ela não está no status PENDENTE.");
        };

        reserva.getPagamento().setStatus(StatusPagamento.PAGO);
        reserva.getPagamento().setFormaEscolhida(FormaPagamento.valueOf(formaPagamento.toString()));

        repository.save(reserva);

    }

    public void cancelar(Long idReserva) throws ReservaNaoExisteException, ReservaNaoPendenteException {
        Reserva reserva = verificaSeExisteERetornaAReserva(idReserva);

        if(!verificaSeReservaPendente(reserva)){
            throw new ReservaNaoPendenteException("Não é possível realizar o cancelamento para esta reserva, pois ela não está no status PENDENTE.");
        };

        reserva.getPagamento().setStatus(StatusPagamento.CANCELADO);
        repository.save(reserva);
    }

    public void estornar(Long idReserva) throws ReservaNaoExisteException, ReservaNaoPagaException {
        Reserva reserva = verificaSeExisteERetornaAReserva(idReserva);
        if(!verificaSeReservaPaga(reserva)){
            throw new ReservaNaoPagaException();
        };
        reserva.getPagamento().setStatus(StatusPagamento.ESTORNADO);
        reserva.getPagamento().setFormaEscolhida(null);
        repository.save(reserva);
    }



    private boolean verificaSeReservaPendente(Reserva reserva) throws ReservaNaoPendenteException {
        StatusPagamento status = reserva.getPagamento().getStatus();
        if(!status.equals(StatusPagamento.PENDENTE)){
            return false;
        }
        return true;
    }

    private Reserva verificaSeExisteERetornaAReserva(Long idReserva) throws ReservaNaoExisteException {
        if(!repository.existsById(idReserva)) throw new ReservaNaoExisteException(idReserva);

        Reserva reserva = repository.findById(idReserva).get();
        return reserva;
    }

    private boolean verificaSeReservaPaga(Reserva reserva) {
        StatusPagamento status = reserva.getPagamento().getStatus();
        if(!status.equals(StatusPagamento.PAGO)){
            return false;
        }
        return true;
    }
}
