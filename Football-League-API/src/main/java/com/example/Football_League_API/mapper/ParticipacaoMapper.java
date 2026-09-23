package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.response.ParticipacaoResponseDto;
import com.example.Football_League_API.entity.Participacao;
import org.springframework.stereotype.Component;

@Component
public class ParticipacaoMapper {

    public ParticipacaoResponseDto toResponseDto(Participacao entity) {
        return ParticipacaoResponseDto.builder()
                .id(entity.getId())
                .temporadaId(entity.getTemporada().getId())
                .timeId(entity.getTime().getId())
                .timeNome(entity.getTime().getNome())
                .build();
    }
}
