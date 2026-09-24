package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.request.PartidaRequestDto;
import com.example.Football_League_API.dto.response.PartidaResponseDto;
import com.example.Football_League_API.entity.Partida;
import org.springframework.stereotype.Component;

@Component
public class PartidaMapper {

    public PartidaResponseDto toResponseDto(Partida partida) {
        return PartidaResponseDto.builder()
                .id(partida.getId())
                .temporadaId(partida.getTemporada().getId())
                .rodada(partida.getRodada())
                .dataHora(partida.getDataHora())
                .timeMandanteId(partida.getTimeMandante().getId())
                .timeMandanteNome(partida.getTimeMandante().getNome())
                .timeVisitanteId(partida.getTimeVisitante().getId())
                .timeVisitanteNome(partida.getTimeVisitante().getNome())
                .golsMandante(partida.getGolsMandante())
                .golsVisitante(partida.getGolsVisitante())
                .status(partida.getStatus())
                .local(partida.getLocal())
                .build();
    }

    public Partida toEntity(PartidaRequestDto dto) {
        return Partida.builder()
                .rodada(dto.getRodada())
                .dataHora(dto.getDataHora())
                .golsMandante(dto.getGolsMandante())
                .golsVisitante(dto.getGolsVisitante())
                .status(dto.getStatus())
                .local(dto.getLocal())
                .build();
    }
}
