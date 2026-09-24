package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.request.JogadorRequestDto;
import com.example.Football_League_API.dto.response.JogadorResponseDto;
import com.example.Football_League_API.entity.Jogador;
import com.example.Football_League_API.entity.Time;
import org.springframework.stereotype.Component;

/**
 * Responsável pela conversão entre Jogador (entidade JPA) e seus DTOs.
 * Particularidade: Jogador pode estar associado a um Time (nullable) ou sem time.
 * Na resposta, retorna além do timeId, também o timeNome para evitar N+1 queries.
 */
@Component
public class JogadorMapper {

    /**
     * Converte DTO (POST) em entidade Jogador.
     * Requer Time como parâmetro (pode ser null, pois time é opcional).
     * Usado quando criar jogador sem time ou vinculado a um time.
     */
    public Jogador toEntity(JogadorRequestDto dto, Time time) {
        return Jogador.builder()
                .nome(dto.getNome())
                .time(time) // Pode ser null (jogador sem time ainda)
                .nacionalidade(dto.getNacionalidade())
                .posicao(dto.getPosicao()) // Enum: GOLEIRO, DEFESA, MEIO_CAMPO, ATACANTE
                .numeroCamisa(dto.getNumeroCamisa())
                .dataNascimento(dto.getDataNascimento())
                .build();
    }

    /**
     * Converte Jogador em DTO de resposta (GET).
     * Retorna dados do jogador + IDs/nomes do Time (se existir).
     * Verifica null no time para evitar NullPointerException em jogadores sem time.
     */
    public JogadorResponseDto toResponseDto(Jogador entity) {
        return JogadorResponseDto.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .timeId(entity.getTime() != null ? entity.getTime().getId() : null) // Null se sem time
                .timeNome(entity.getTime() != null ? entity.getTime().getNome() : null) // Denormalizado: evita query extra
                .nacionalidade(entity.getNacionalidade())
                .posicao(entity.getPosicao())
                .numeroCamisa(entity.getNumeroCamisa())
                .dataNascimento(entity.getDataNascimento())
                .build();
    }

    /**
     * Atualiza Jogador com dados do DTO (PUT).
     * Requer Time como parâmetro (pode ser null para desassociar do time).
     */
    public void updateEntity(JogadorRequestDto dto, Jogador entity, Time time) {
        entity.setNome(dto.getNome());
        entity.setTime(time); // Pode desassociar passando null
        entity.setNacionalidade(dto.getNacionalidade());
        entity.setPosicao(dto.getPosicao());
        entity.setNumeroCamisa(dto.getNumeroCamisa());
        entity.setDataNascimento(dto.getDataNascimento());
    }
}
