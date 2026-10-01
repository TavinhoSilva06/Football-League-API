package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.request.PartidaRequestDto;
import com.example.Football_League_API.dto.response.PartidaResponseDto;
import com.example.Football_League_API.entity.Partida;
import org.springframework.stereotype.Component;

/**
 * Responsável pela conversão entre Partida (entidade JPA) e seus DTOs.
 * Partida é um relacionamento complexo: envolve Temporada, Time Mandante e Time Visitante.
 * Na resposta, desnormaliza nomes dos times para evitar que o cliente precise fazer queries extras.
 */
@Component
public class PartidaMapper {

    /**
     * Converte Partida em DTO de resposta (GET).
     * Retorna dados completos da partida incluindo IDs e nomes dos dois times.
     * Denormalização: evita que client precise fazer 2 queries adicionais (uma por time).
     */
    public PartidaResponseDto toResponseDto(Partida partida) {
        return PartidaResponseDto.builder()
                .id(partida.getId())
                .temporadaId(partida.getTemporada().getId()) // ID da temporada em que ocorre
                .rodada(partida.getRodada()) // Número da rodada (ex: 1, 2, 3...)
                .dataHora(partida.getDataHora())
                .timeMandanteId(partida.getTimeMandante().getId())
                .timeMandanteNome(partida.getTimeMandante().getNome()) // Denormalizado
                .timeVisitanteId(partida.getTimeVisitante().getId())
                .timeVisitanteNome(partida.getTimeVisitante().getNome()) // Denormalizado
                .golsMandante(partida.getGolsMandante())
                .golsVisitante(partida.getGolsVisitante())
                .status(partida.getStatus()) // Enum: AGENDADA, EM_ANDAMENTO, FINALIZADA, etc
                .local(partida.getLocal()) // Estádio/local da partida
                .build();
    }

    /**
     * Converte DTO (POST) em entidade Partida.
     * Nota: Temporada, TimeMandante e TimeVisitante são setados no Service (após validação).
     * Este mapper só mapeia dados simples do DTO.
     */
    public Partida toEntity(PartidaRequestDto dto) {
        return Partida.builder()
                .rodada(dto.getRodada())
                .dataHora(dto.getDataHora())
                .golsMandante(dto.getGolsMandante()) // Nullable (não tem gols até fim da partida)
                .golsVisitante(dto.getGolsVisitante()) // Nullable
                .status(dto.getStatus()) // Se null, Service define como AGENDADA
                .local(dto.getLocal())
                .build(); // Sem relacionamentos (serão setados no Service)
    }
}
