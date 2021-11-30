package com.github.amanda.reservas.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.amanda.reservas.api.domain.Endereco;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AtualizarUsuarioRequest {

    @NotBlank
    private String nome;
    @NotBlank
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String senha;
    @NotNull
    private LocalDate dataNascimento;
    @Valid
    private Endereco endereco;
}
