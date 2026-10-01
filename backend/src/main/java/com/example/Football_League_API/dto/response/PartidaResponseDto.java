package com.example.Football_League_API.dto.response;

import com.example.Football_League_API.enu.StatusPartida;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartidaResponseDto {

    private Long id;

    private Long temporadaId;

    private Integer rodada;

    private LocalDateTime dataHora;

    private Long timeMandanteId;

    private String timeMandanteNome;

    private Long timeVisitanteId;

    private String timeVisitanteNome;

    private Integer golsMandante;

    private Integer golsVisitante;

    private StatusPartida status;

    private String local;
}
