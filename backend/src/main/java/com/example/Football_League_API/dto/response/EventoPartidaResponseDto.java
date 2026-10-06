package com.example.Football_League_API.dto.response;

import com.example.Football_League_API.enu.TipoEvento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoPartidaResponseDto {

    private Long id;
    private Long partidaId;
    private Long jogadorId;
    private String jogadorNome;
    private Long temporadaId;
    private TipoEvento tipo;
    private Integer minuto;
    private String descricao;
}
