package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.response.PalpiteResponseDto;
import com.example.Football_League_API.entity.Palpite;
import org.springframework.stereotype.Component;

@Component
public class PalpiteMapper {

    public PalpiteResponseDto toResponseDto(Palpite entity) {
        return PalpiteResponseDto.builder()
                .id(entity.getId())
                .partidaId(entity.getPartida() == null ? null : entity.getPartida().getId())
                .usuarioId(entity.getUsuario() == null ? null : entity.getUsuario().getId())
                .usuarioNome(entity.getUsuario() == null ? null : entity.getUsuario().getNome())
                .golsMandante(entity.getGolsMandante())
                .golsVisitante(entity.getGolsVisitante())
                .acerto(entity.getAcerto())
                .pontosGanhos(entity.getPontosGanhos())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
