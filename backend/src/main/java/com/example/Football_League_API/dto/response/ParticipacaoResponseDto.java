package com.example.Football_League_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipacaoResponseDto {

    private Long id;

    private Long temporadaId;

    private Long timeId;

    private String timeNome;
}
