package io.github.cwireset.tcc.repository;

import io.github.cwireset.tcc.domain.Imovel;
import io.github.cwireset.tcc.request.CadastrarImovelRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImovelRepository extends JpaRepository<Imovel, Long> {

    Imovel save(Imovel imovel);
}
