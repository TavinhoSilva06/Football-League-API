package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.request.TemporadaRequestDto;
import com.example.Football_League_API.dto.response.TemporadaResponseDto;
import com.example.Football_League_API.entity.Campeonato;
import com.example.Football_League_API.entity.Temporada;
import org.springframework.stereotype.Component;

@Component
public class TemporadaMapper {

    public TemporadaResponseDto toResponseDto(Temporada entity) {
        if (entity == null) {
            return null;
        }
        return TemporadaResponseDto.builder()
                .id(entity.getId())
                .campeonatoId(entity.getCampeonato() != null ? entity.getCampeonato().getId() : null)
                .nome(entity.getNome())
                .dataInicio(entity.getDataInicio())
                .dataFim(entity.getDataFim())
                .status(entity.getStatus())
                .build();
    }

    public Temporada toEntity(TemporadaRequestDto dto, Campeonato campeonato) {
        if (dto == null) {
            return null;
        }
        return Temporada.builder()
                .campeonato(campeonato)
                .nome(dto.getNome())
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .status(dto.getStatus())
                .build();
    }

    public void updateEntity(TemporadaRequestDto dto, Temporada entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNome(dto.getNome());
        entity.setDataInicio(dto.getDataInicio());
        entity.setDataFim(dto.getDataFim());
        entity.setStatus(dto.getStatus());
    }
}
