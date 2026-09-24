package com.example.Football_League_API.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoRequestDto {

    @NotNull(message = "Gols do mandante são obrigatórios")
    @Min(value = 0, message = "Gols do mandante não pode ser negativo")
    private Integer golsMandante;

    @NotNull(message = "Gols do visitante são obrigatórios")
    @Min(value = 0, message = "Gols do visitante não pode ser negativo")
    private Integer golsVisitante;
}
