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
public class JogoPalpiteResponseDto {

    private Long partidaId;
    private Integer rodada;
    private Long mandanteId;
    private String mandanteNome;
    private String mandanteEscudoUrl;
    private Long visitanteId;
    private String visitanteNome;
    private String visitanteEscudoUrl;
    private LocalDateTime dataHora;
    private LocalDateTime dataLimitePalpite;
    private StatusPartida status;
    private Integer golsMandante;
    private Integer golsVisitante;
    private Long palpiteId;
    private Integer palpiteMandante;
    private Integer palpiteVisitante;
    private Boolean acerto;
    private Integer pontosGanhos;
    private Boolean bloqueado;
}
