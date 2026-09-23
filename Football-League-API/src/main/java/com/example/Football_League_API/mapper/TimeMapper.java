package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.request.TimeRequestDto;
import com.example.Football_League_API.dto.response.TimeResponseDto;
import com.example.Football_League_API.entity.Time;
import org.springframework.stereotype.Component;

@Component
public class TimeMapper {

    public Time toEntity(TimeRequestDto dto) {
        return Time.builder()
                .nome(dto.getNome())
                .sigla(dto.getSigla())
                .pais(dto.getPais())
                .cidade(dto.getCidade())
                .estadio(dto.getEstadio())
                .escudoUrl(dto.getEscudoUrl())
                .build();
    }

    public TimeResponseDto toResponseDto(Time entity) {
        return TimeResponseDto.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .sigla(entity.getSigla())
                .pais(entity.getPais())
                .cidade(entity.getCidade())
                .estadio(entity.getEstadio())
                .escudoUrl(entity.getEscudoUrl())
                .build();
    }

    public void updateEntity(TimeRequestDto dto, Time entity) {
        entity.setNome(dto.getNome());
        entity.setSigla(dto.getSigla());
        entity.setPais(dto.getPais());
        entity.setCidade(dto.getCidade());
        entity.setEstadio(dto.getEstadio());
        entity.setEscudoUrl(dto.getEscudoUrl());
    }
}
