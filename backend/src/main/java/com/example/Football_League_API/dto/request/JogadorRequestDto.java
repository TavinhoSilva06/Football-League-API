package com.example.Football_League_API.dto.request;

import com.example.Football_League_API.enu.PosicaoJogador;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JogadorRequestDto {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private Long timeId;

    private String nacionalidade;

    private PosicaoJogador posicao;

    private Integer numeroCamisa;

    private LocalDate dataNascimento;
}
