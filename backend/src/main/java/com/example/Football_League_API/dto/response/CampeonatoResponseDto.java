package com.example.Football_League_API.dto.response;

import com.example.Football_League_API.enu.FormatoCampeonato;
import com.example.Football_League_API.enu.TipoCampeonato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampeonatoResponseDto {

    private Long id;

    private String nome;

    private String pais;

    private TipoCampeonato tipo;

    private String descricao;

    private FormatoCampeonato formato;
}
