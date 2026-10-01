package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.request.TemporadaRequestDto;
import com.example.Football_League_API.dto.response.TemporadaResponseDto;
import com.example.Football_League_API.entity.Campeonato;
import com.example.Football_League_API.entity.Temporada;
import org.springframework.stereotype.Component;

/**
 * Responsável pela conversão entre Temporada (entidade JPA) e seus DTOs.
 * Diferente de CampeonatoMapper, recebe o Campeonato como parâmetro
 * porque a Temporada sempre precisa estar associada a um Campeonato (M:1).
 */
@Component
public class TemporadaMapper {

    /**
     * Converte Temporada em DTO de resposta (GET).
     * Extrai apenas o ID do Campeonato (não inclui campeonato completo na resposta).
     */
    public TemporadaResponseDto toResponseDto(Temporada entity) {
        if (entity == null) {
            return null;
        }
        return TemporadaResponseDto.builder()
                .id(entity.getId())
                .campeonatoId(entity.getCampeonato() != null ? entity.getCampeonato().getId() : null) // Só ID para não duplicar dados
                .nome(entity.getNome())
                .dataInicio(entity.getDataInicio())
                .dataFim(entity.getDataFim())
                .numRodadas(entity.getNumRodadas()) // Número máximo de rodadas
                .status(entity.getStatus()) // Enum: PLANEJADA, EM_ANDAMENTO, ENCERRADA
                .build();
    }

    /**
     * Converte DTO (POST) em entidade Temporada.
     * Requer o Campeonato como parâmetro (veio do validador no Service).
     */
    public Temporada toEntity(TemporadaRequestDto dto, Campeonato campeonato) {
        if (dto == null) {
            return null;
        }
        return Temporada.builder()
                .campeonato(campeonato) // Associa ao Campeonato (relacionamento M:1)
                .nome(dto.getNome())
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .numRodadas(dto.getNumRodadas()) // Número máximo de rodadas
                .status(dto.getStatus())
                .build();
    }

    /**
     * Atualiza Temporada existente com dados do DTO (PUT).
     * Nota: Campeonato não é atualizado (não pode mover temporada entre campeonatos).
     */
    public void updateEntity(TemporadaRequestDto dto, Temporada entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNome(dto.getNome());
        entity.setDataInicio(dto.getDataInicio());
        entity.setDataFim(dto.getDataFim());
        entity.setNumRodadas(dto.getNumRodadas()); // Atualiza número de rodadas
        entity.setStatus(dto.getStatus());
        // campeonato não é alterado (integridade referencial)
    }
}
