package com.example.Football_League_API.dto.request;

import com.example.Football_League_API.enu.TipoEvento;
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
public class EventoPartidaRequestDto {

    @NotNull(message = "ID do jogador é obrigatório")
    private Long jogadorId;

    @NotNull(message = "Tipo de evento é obrigatório")
    private TipoEvento tipo;

    @Min(value = 0, message = "Minuto não pode ser negativo")
    private Integer minuto;

    private String descricao;
}
