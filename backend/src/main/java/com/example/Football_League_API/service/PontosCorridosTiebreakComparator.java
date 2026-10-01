package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.response.ClassificacaoEntradaDto;
import java.util.Comparator;

/**
 * Comparador para desempate na tabela de pontos corridos (5 critérios sequenciais).
 * Usado para ordenar times quando há empate em pontos.
 *
 * Critérios de desempate em ordem:
 * 1. Pontos totais (descendente)
 * 2. Vitórias (descendente)
 * 3. Saldo de gols (descendente)
 * 4. Gols pró (descendente)
 * 5. Nome do time (ascendente alfabético)
 */
public class PontosCorridosTiebreakComparator implements Comparator<ClassificacaoEntradaDto> {

    @Override
    public int compare(ClassificacaoEntradaDto a, ClassificacaoEntradaDto b) {
        // 1. Pontos: maior vence (descendente)
        int pontosCompare = b.getPontos().compareTo(a.getPontos());
        if (pontosCompare != 0) {
            return pontosCompare;
        }

        // 2. Vitórias: maior vence (descendente)
        int vitoriasCompare = b.getVitorias().compareTo(a.getVitorias());
        if (vitoriasCompare != 0) {
            return vitoriasCompare;
        }

        // 3. Saldo de gols: maior vence (descendente)
        int saldoCompare = b.getSaldoGols().compareTo(a.getSaldoGols());
        if (saldoCompare != 0) {
            return saldoCompare;
        }

        // 4. Gols pró: maior vence (descendente)
        int golsProCompare = b.getGolsPro().compareTo(a.getGolsPro());
        if (golsProCompare != 0) {
            return golsProCompare;
        }

        // 5. Nome: ordem alfabética (ascendente)
        return a.getTimeNome().compareToIgnoreCase(b.getTimeNome());
    }
}
