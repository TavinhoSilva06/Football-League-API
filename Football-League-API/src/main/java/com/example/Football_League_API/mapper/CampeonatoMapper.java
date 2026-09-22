package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.request.CampeonatoRequestDto;
import com.example.Football_League_API.dto.response.CampeonatoResponseDto;
import com.example.Football_League_API.entity.Campeonato;
import org.springframework.stereotype.Component;

@Component
public class CampeonatoMapper {

    public CampeonatoResponseDto toResponseDto(Campeonato entity) {
        if (entity == null) {
            return null;
        }
        return CampeonatoResponseDto.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .pais(entity.getPais())
                .tipo(entity.getTipo())
                .descricao(entity.getDescricao())
                .formato(entity.getFormato())
                .build();
    }

    public Campeonato toEntity(CampeonatoRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return Campeonato.builder()
                .nome(dto.getNome())
                .pais(dto.getPais())
                .tipo(dto.getTipo())
                .descricao(dto.getDescricao())
                .formato(dto.getFormato())
                .build();
    }

    public void updateEntity(CampeonatoRequestDto dto, Campeonato entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNome(dto.getNome());
        entity.setPais(dto.getPais());
        entity.setTipo(dto.getTipo());
        entity.setDescricao(dto.getDescricao());
        entity.setFormato(dto.getFormato());
    }
}
