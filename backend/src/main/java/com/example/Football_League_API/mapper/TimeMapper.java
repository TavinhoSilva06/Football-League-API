package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.request.TimeRequestDto;
import com.example.Football_League_API.dto.response.TimeResponseDto;
import com.example.Football_League_API.entity.Time;
import org.springframework.stereotype.Component;

/**
 * Responsável pela conversão entre Time (entidade JPA) e seus DTOs.
 * Time é uma entidade autossuficiente (sem dependências obrigatórias de outras entidades).
 * Não inclui relacionamentos complexos como lista de Jogadores ou Participações na resposta.
 */
@Component
public class TimeMapper {

    /**
     * Converte DTO de requisição (POST) em entidade Time.
     * Simples e direto: sem parâmetros adicionais necessários.
     */
    public Time toEntity(TimeRequestDto dto) {
        return Time.builder()
                .nome(dto.getNome())
                .sigla(dto.getSigla()) // Código curto (ex: "MU", "LIV")
                .pais(dto.getPais())
                .cidade(dto.getCidade())
                .estadio(dto.getEstadio())
                .escudoUrl(dto.getEscudoUrl())
                .build(); // Sem ID (será gerado)
    }

    /**
     * Converte entidade Time em DTO de resposta (GET).
     * Retorna todos os dados públicos do time (sem relacionamentos internos).
     */
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

    /**
     * Atualiza Time existente com dados do DTO (PUT).
     * Todos os campos são atualizáveis (time pode mudar de estádio, logo, etc).
     */
    public void updateEntity(TimeRequestDto dto, Time entity) {
        entity.setNome(dto.getNome());
        entity.setSigla(dto.getSigla());
        entity.setPais(dto.getPais());
        entity.setCidade(dto.getCidade());
        entity.setEstadio(dto.getEstadio());
        entity.setEscudoUrl(dto.getEscudoUrl());
    }
}
