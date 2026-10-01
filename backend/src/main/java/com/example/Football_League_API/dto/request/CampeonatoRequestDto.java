package com.example.Football_League_API.dto.request;

import com.example.Football_League_API.enu.TipoCampeonato;
import com.example.Football_League_API.enu.FormatoCampeonato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampeonatoRequestDto {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String pais;

    private TipoCampeonato tipo;

    private String descricao;

    private FormatoCampeonato formato;
}
