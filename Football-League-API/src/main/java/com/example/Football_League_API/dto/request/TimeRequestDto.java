package com.example.Football_League_API.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeRequestDto {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String sigla;

    private String pais;

    private String cidade;

    private String estadio;

    private String escudoUrl;
}
