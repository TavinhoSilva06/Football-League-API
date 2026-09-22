package com.example.Football_League_API.dto.request;

import com.example.Football_League_API.enu.StatusTemporada;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporadaRequestDto {

    @NotNull(message = "ID do campeonato é obrigatório")
    private Long campeonatoId;

    @NotBlank(message = "Nome da temporada é obrigatório")
    private String nome;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private StatusTemporada status;
}
