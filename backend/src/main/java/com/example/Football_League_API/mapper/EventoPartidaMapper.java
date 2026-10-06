package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.request.EventoPartidaRequestDto;
import com.example.Football_League_API.dto.response.EventoPartidaResponseDto;
import com.example.Football_League_API.entity.EventoPartida;
import org.springframework.stereotype.Component;

@Component
public class EventoPartidaMapper {

    public EventoPartidaResponseDto toResponseDto(EventoPartida entity) {
        return EventoPartidaResponseDto.builder()
                .id(entity.getId())
                .partidaId(entity.getPartida().getId())
                .jogadorId(entity.getJogador().getId())
                .jogadorNome(entity.getJogador().getNome())
                .temporadaId(entity.getTemporada().getId())
                .tipo(entity.getTipo())
                .minuto(entity.getMinuto())
                .descricao(entity.getDescricao())
                .build();
    }

    public EventoPartida toEntity(EventoPartidaRequestDto dto, EventoPartida entity) {
        if (entity == null) {
            entity = new EventoPartida();
        }
        entity.setTipo(dto.getTipo());
        entity.setMinuto(dto.getMinuto());
        entity.setDescricao(dto.getDescricao());
        return entity;
    }
}
