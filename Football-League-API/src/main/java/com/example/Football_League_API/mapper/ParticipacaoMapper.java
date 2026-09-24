package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.response.ParticipacaoResponseDto;
import com.example.Football_League_API.entity.Participacao;
import org.springframework.stereotype.Component;

/**
 * Responsável pela conversão de Participacao (entidade JPA) em DTO de resposta.
 * Participacao é um relacionamento M:N (via tabela associativa) entre Time e Temporada.
 * Não precisa de toEntity() porque a criação é feita direto no Service (simples).
 */
@Component
public class ParticipacaoMapper {

    /**
     * Converte Participacao em DTO de resposta (GET).
     * Retorna: ID da participação, ID da temporada, ID e nome do time.
     * O nome do time é denormalizado aqui para economizar queries.
     */
    public ParticipacaoResponseDto toResponseDto(Participacao entity) {
        return ParticipacaoResponseDto.builder()
                .id(entity.getId())
                .temporadaId(entity.getTemporada().getId()) // Referência à temporada
                .timeId(entity.getTime().getId()) // Referência ao time
                .timeNome(entity.getTime().getNome()) // Denormalizado para facilitar leitura
                .build();
    }
}
