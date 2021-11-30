package com.github.amanda.reservas.api.repository;

import com.github.amanda.reservas.api.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {

    Usuario save(Usuario usuario);
    Page<Usuario> findAll(Pageable pageable);
    boolean existsById(Long id);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    Usuario findByCpf(String cpf);
    Optional<Usuario>findById(Long id);



}
