package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.response.ClassificacaoEntradaDto;
import com.example.Football_League_API.entity.Campeonato;
import com.example.Football_League_API.enu.FormatoCampeonato;
import java.util.Comparator;

/**
 * Resolver de estratégia de desempate de acordo com o formato do campeonato.
 * Retorna o comparador apropriado para ordenar a tabela de classificação.
 *
 * Atualmente suporta:
 * - PONTOS_CORRIDOS: ordem por pontos, vitórias, saldo, gols pró, nome
 * - MATA_MATA: lança exceção (não há tabela de pontos em mata-mata)
 */
public class TiebreakStrategyResolver {

    /**
     * Resolve o comparador de desempate baseado no formato do campeonato.
     * @param campeonato entidade do campeonato com seu formato definido
     * @return Comparator<ClassificacaoEntradaDto> apropriado ao formato
     * @throws IllegalArgumentException se o formato é MATA_MATA (sem tabela de pontos)
     */
    public static Comparator<ClassificacaoEntradaDto> resolve(Campeonato campeonato) {
        if (campeonato.getFormato() == FormatoCampeonato.PONTOS_CORRIDOS) {
            return new PontosCorridosTiebreakComparator();
        }

        if (campeonato.getFormato() == FormatoCampeonato.MATA_MATA) {
            throw new IllegalArgumentException(
                "Campeonato em formato MATA_MATA não possui tabela de classificação por pontos. " +
                "Consulte o chaveamento em /temporadas/{temporadaId}/confrontos"
            );
        }

        throw new IllegalArgumentException("Formato de campeonato desconhecido: " + campeonato.getFormato());
    }
}
