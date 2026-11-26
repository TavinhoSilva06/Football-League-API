package com.example.Football_League_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RodadaPalpitesResponseDto {

    private Long temporadaId;
    private Integer rodada;
    private List<JogoPalpiteResponseDto> jogos;
    private Integer palpitesEnviados;
    private Integer acertos;
    private Integer pontosGanhos;
}
