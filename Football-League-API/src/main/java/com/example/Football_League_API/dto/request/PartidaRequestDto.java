package com.example.Football_League_API.dto.request;

import com.example.Football_League_API.enu.StatusPartida;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartidaRequestDto {

    @NotNull(message = "Rodada é obrigatória")
    private Integer rodada;

    private LocalDateTime dataHora;

    @NotNull(message = "Time mandante é obrigatório")
    private Long timeMandanteId;

    @NotNull(message = "Time visitante é obrigatório")
    private Long timeVisitanteId;

    private Integer golsMandante;

    private Integer golsVisitante;

    private StatusPartida status;

    private String local;
}
