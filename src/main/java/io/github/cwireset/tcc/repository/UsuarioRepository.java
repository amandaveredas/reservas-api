package io.github.cwireset.tcc.repository;

import io.github.cwireset.tcc.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {

    Usuario save(Usuario usuario);
    Usuario findByEmailContaining(String email);
    Usuario findByCpfContaining(String cpf);
}
