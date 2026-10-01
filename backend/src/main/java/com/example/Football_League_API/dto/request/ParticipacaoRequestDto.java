package com.example.Football_League_API.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipacaoRequestDto {

    @NotNull(message = "ID do time é obrigatório")
    private Long timeId;
}
