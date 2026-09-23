package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.request.JogadorRequestDto;
import com.example.Football_League_API.dto.response.JogadorResponseDto;
import com.example.Football_League_API.entity.Jogador;
import com.example.Football_League_API.entity.Time;
import org.springframework.stereotype.Component;

@Component
public class JogadorMapper {

    public Jogador toEntity(JogadorRequestDto dto, Time time) {
        return Jogador.builder()
                .nome(dto.getNome())
                .time(time)
                .nacionalidade(dto.getNacionalidade())
                .posicao(dto.getPosicao())
                .numeroCamisa(dto.getNumeroCamisa())
                .dataNascimento(dto.getDataNascimento())
                .build();
    }

    public JogadorResponseDto toResponseDto(Jogador entity) {
        return JogadorResponseDto.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .timeId(entity.getTime() != null ? entity.getTime().getId() : null)
                .timeNome(entity.getTime() != null ? entity.getTime().getNome() : null)
                .nacionalidade(entity.getNacionalidade())
                .posicao(entity.getPosicao())
                .numeroCamisa(entity.getNumeroCamisa())
                .dataNascimento(entity.getDataNascimento())
                .build();
    }

    public void updateEntity(JogadorRequestDto dto, Jogador entity, Time time) {
        entity.setNome(dto.getNome());
        entity.setTime(time);
        entity.setNacionalidade(dto.getNacionalidade());
        entity.setPosicao(dto.getPosicao());
        entity.setNumeroCamisa(dto.getNumeroCamisa());
        entity.setDataNascimento(dto.getDataNascimento());
    }
}
