package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.request.EstatisticaJogadorRequestDto;
import com.example.Football_League_API.dto.response.EstatisticaJogadorResponseDto;
import com.example.Football_League_API.entity.EstatisticaJogador;
import org.springframework.stereotype.Component;

@Component
public class EstatisticaJogadorMapper {

    public EstatisticaJogadorResponseDto toResponseDto(EstatisticaJogador entity) {
        return EstatisticaJogadorResponseDto.builder()
                .id(entity.getId())
                .jogadorId(entity.getJogador().getId())
                .jogadorNome(entity.getJogador().getNome())
                .temporadaId(entity.getTemporada().getId())
                .temporadaNome(entity.getTemporada().getNome())
                .jogos(entity.getJogos())
                .gols(entity.getGols())
                .assistencias(entity.getAssistencias())
                .cartoesAmarelos(entity.getCartoesAmarelos())
                .cartoesVermelhos(entity.getCartoesVermelhos())
                .build();
    }

    public void updateEntityFromDto(EstatisticaJogadorRequestDto dto, EstatisticaJogador entity) {
        entity.setJogos(dto.getJogos());
        entity.setGols(dto.getGols());
        entity.setAssistencias(dto.getAssistencias());
        entity.setCartoesAmarelos(dto.getCartoesAmarelos());
        entity.setCartoesVermelhos(dto.getCartoesVermelhos());
    }
}
