package io.github.cwireset.tcc.repository;

import io.github.cwireset.tcc.domain.Anuncio;
import io.github.cwireset.tcc.domain.Imovel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {

    Anuncio save(Anuncio anuncio);
    boolean existsByImovel(Imovel imovel);
    Page<Anuncio> findAll(Pageable pageable);
}
