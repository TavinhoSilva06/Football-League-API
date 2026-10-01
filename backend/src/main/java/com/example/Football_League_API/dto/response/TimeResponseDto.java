package com.example.Football_League_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeResponseDto {

    private Long id;

    private String nome;

    private String sigla;

    private String pais;

    private String cidade;

    private String estadio;

    private String escudoUrl;
}
