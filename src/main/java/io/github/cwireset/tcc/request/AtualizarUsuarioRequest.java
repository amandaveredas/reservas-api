package io.github.cwireset.tcc.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.cwireset.tcc.domain.Endereco;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AtualizarUsuarioRequest {

    @NotNull
    private String nome;
    @NotNull
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private String senha;
    @NotNull
    private LocalDate dataNascimento;
    @Valid
    private Endereco endereco;
}
