package com.example.Football_League_API.dto.response;

import com.example.Football_League_API.enu.PosicaoJogador;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JogadorResponseDto {

    private Long id;

    private String nome;

    private Long timeId;

    private String timeNome;

    private String nacionalidade;

    private PosicaoJogador posicao;

    private Integer numeroCamisa;

    private LocalDate dataNascimento;
}
