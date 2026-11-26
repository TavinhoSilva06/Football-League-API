package com.example.Football_League_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PalpiteResponseDto {

    private Long id;
    private Long partidaId;
    private Long usuarioId;
    private String usuarioNome;
    private Integer golsMandante;
    private Integer golsVisitante;
    private Boolean acerto;
    private Integer pontosGanhos;
    private LocalDateTime criadoEm;
}
