package io.github.cwireset.tcc.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String nome;
    @NotNull
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private String senha;
    @NotNull
    @Pattern(regexp = "[0-9]{11}",message = "O CPF deve ser informado no formato 99999999999.")
    private String cpf;
    @NotNull
    private LocalDate dataNascimento;
    private String avatar;

    @Valid
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) && Objects.equals(nome, usuario.nome) && Objects.equals(email, usuario.email) && Objects.equals(senha, usuario.senha) && Objects.equals(cpf, usuario.cpf) && Objects.equals(dataNascimento, usuario.dataNascimento) && Objects.equals(avatar, usuario.avatar) && Objects.equals(endereco, usuario.endereco);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, email, senha, cpf, dataNascimento, avatar, endereco);
    }
}
