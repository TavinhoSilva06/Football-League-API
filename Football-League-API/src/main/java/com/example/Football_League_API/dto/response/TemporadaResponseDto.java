package com.example.Football_League_API.dto.response;

import com.example.Football_League_API.enu.StatusTemporada;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporadaResponseDto {

    private Long id;

    private Long campeonatoId;

    private String nome;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private Integer numRodadas; // Número máximo de rodadas da temporada

    private StatusTemporada status;
}
