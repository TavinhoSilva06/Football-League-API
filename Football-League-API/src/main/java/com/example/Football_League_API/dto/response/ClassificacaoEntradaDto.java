package com.example.Football_League_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa uma linha da tabela de classificação de uma temporada.
 * Contém dados agregados de um time em um campeonato.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassificacaoEntradaDto {

    private Integer posicao; // Posição na tabela (1º, 2º, 3º, etc)

    private Long timeId; // ID do time

    private String timeNome; // Nome do time para exibição

    private Integer jogos; // Número de partidas jogadas

    private Integer vitorias; // Vitórias

    private Integer empates; // Empates

    private Integer derrotas; // Derrotas

    private Integer golsPro; // Gols marcados

    private Integer golsContra; // Gols sofridos

    private Integer saldoGols; // Diferença de gols (golsPro - golsContra)

    private Integer pontos; // Pontos totais (vitória=3, empate=1, derrota=0)
}
