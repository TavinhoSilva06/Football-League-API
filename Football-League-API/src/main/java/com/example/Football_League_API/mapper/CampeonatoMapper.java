package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.request.CampeonatoRequestDto;
import com.example.Football_League_API.dto.response.CampeonatoResponseDto;
import com.example.Football_League_API.entity.Campeonato;
import org.springframework.stereotype.Component;

/**
 * Responsável pela conversão entre Campeonato (entidade JPA) e seus DTOs (request/response).
 * Mapeia dados sem incluir relacionamentos complexos (como lista de temporadas),
 * mantendo a resposta leve e focada no recurso principal.
 */
@Component
public class CampeonatoMapper {

    /**
     * Converte a entidade Campeonato em DTO de resposta (GET).
     * Retorna null se a entidade for null (segurança contra NPE).
     */
    public CampeonatoResponseDto toResponseDto(Campeonato entity) {
        if (entity == null) {
            return null; // Evita erro ao converter null
        }
        return CampeonatoResponseDto.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .pais(entity.getPais())
                .tipo(entity.getTipo()) // Enum: LIGA_NACIONAL, CONTINENTAL, COPA
                .descricao(entity.getDescricao())
                .formato(entity.getFormato()) // Enum: PONTOS_CORRIDOS, etc
                .build();
    }

    /**
     * Converte DTO de requisição (POST) em entidade Campeonato (sem ID, pois é gerado).
     * Retorna null se DTO for null (segurança).
     */
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
                .build(); // Sem ID (será gerado pelo banco)
    }

    /**
     * Atualiza uma entidade existente com dados de um DTO (PUT).
     * Mantém o ID original, atualizando apenas campos específicos.
     */
    public void updateEntity(CampeonatoRequestDto dto, Campeonato entity) {
        if (dto == null || entity == null) {
            return; // Segurança: evita NullPointerException
        }
        entity.setNome(dto.getNome());
        entity.setPais(dto.getPais());
        entity.setTipo(dto.getTipo());
        entity.setDescricao(dto.getDescricao());
        entity.setFormato(dto.getFormato());
    }
}
