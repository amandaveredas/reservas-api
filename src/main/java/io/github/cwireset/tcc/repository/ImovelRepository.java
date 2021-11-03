package io.github.cwireset.tcc.repository;

import io.github.cwireset.tcc.domain.Imovel;
import io.github.cwireset.tcc.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImovelRepository extends JpaRepository<Imovel, Long> {

    Imovel save(Imovel imovel);
    Page<Imovel> findAll(Pageable pageable);
    Page<Imovel> findAllByProprietario(Pageable pageable, Usuario proprietario);
    Optional<Imovel> findById(Long id);
    boolean existsById(Long id);
    void deleteById(Long id);


}
