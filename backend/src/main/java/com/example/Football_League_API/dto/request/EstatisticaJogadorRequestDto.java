package com.example.Football_League_API.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstatisticaJogadorRequestDto {

    @NotNull(message = "Número de jogos é obrigatório")
    @Min(value = 0, message = "Número de jogos não pode ser negativo")
    private Integer jogos;

    @NotNull(message = "Número de gols é obrigatório")
    @Min(value = 0, message = "Número de gols não pode ser negativo")
    private Integer gols;

    @NotNull(message = "Número de assistências é obrigatório")
    @Min(value = 0, message = "Número de assistências não pode ser negativo")
    private Integer assistencias;

    @NotNull(message = "Número de cartões amarelos é obrigatório")
    @Min(value = 0, message = "Número de cartões amarelos não pode ser negativo")
    private Integer cartoesAmarelos;

    @NotNull(message = "Número de cartões vermelhos é obrigatório")
    @Min(value = 0, message = "Número de cartões vermelhos não pode ser negativo")
    private Integer cartoesVermelhos;
}
