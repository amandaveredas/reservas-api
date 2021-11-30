package com.github.amanda.reservas.api.repository;

import com.github.amanda.reservas.api.domain.Imovel;
import com.github.amanda.reservas.api.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImovelRepository extends JpaRepository<Imovel, Long> {

    Imovel save(Imovel imovel);
    Page<Imovel> findAllByAtivoIsTrue(Pageable pageable);
    Page<Imovel> findAllByAtivoIsTrueAndProprietarioEquals(Usuario proprietario, Pageable pageable);
    Optional<Imovel> findById(Long id);
    boolean existsByIdAndAtivoIsTrue(Long id);
    Page<Imovel> findAllByProprietario(Usuario proprietario, Pageable pageable);


}
