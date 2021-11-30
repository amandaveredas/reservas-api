package com.github.amanda.reservas.api.repository;

import com.github.amanda.reservas.api.domain.Anuncio;
import com.github.amanda.reservas.api.domain.Imovel;
import com.github.amanda.reservas.api.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {

    Anuncio save(Anuncio anuncio);
    boolean existsByImovelAndAtivoIsTrue(Imovel imovel);
    Page<Anuncio> findAllByAtivoIsTrue(Pageable pageable);
    Page<Anuncio> findAllByAtivoIsTrueAndAndAnuncianteEquals(Usuario anunciante, Pageable pageable);
    Optional<Anuncio> findById(Long id);
    boolean existsByIdAndAtivoIsTrue(Long id);
}
