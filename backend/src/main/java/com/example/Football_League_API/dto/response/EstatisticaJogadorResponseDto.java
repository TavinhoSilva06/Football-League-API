package com.example.Football_League_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstatisticaJogadorResponseDto {

    private Long id;
    private Long jogadorId;
    private String jogadorNome;
    private Long temporadaId;
    private String temporadaNome;
    private Integer jogos;
    private Integer gols;
    private Integer assistencias;
    private Integer cartoesAmarelos;
    private Integer cartoesVermelhos;
}
