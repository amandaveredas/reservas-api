package io.github.cwireset.tcc.repository;

import io.github.cwireset.tcc.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {

    Usuario save(Usuario usuario);
    Page<Usuario> findAll(Pageable pageable);
    boolean existsById(Long id);
    boolean existsByCpf(String cpf);
    Usuario findByCpf(String cpf);
    Usuario findByEmail(String email);
}
