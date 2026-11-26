package com.example.Football_League_API.mapper;

import com.example.Football_League_API.dto.response.UsuarioResponseDto;
import com.example.Football_League_API.entity.Usuario;
import com.example.Football_League_API.enu.Patente;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDto toResponseDto(Usuario entity) {
        int pontos = entity.getPontos() == null ? 0 : entity.getPontos();
        Patente patente = Patente.fromPontos(pontos);
        return UsuarioResponseDto.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .role(entity.getRole())
                .pontos(pontos)
                .patente(patente)
                .proximaPatente(patente.proxima())
                .pontosParaProxima(patente.pontosParaProxima(pontos))
                .build();
    }
}
