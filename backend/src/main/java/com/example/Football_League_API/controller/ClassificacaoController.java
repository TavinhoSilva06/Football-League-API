package com.example.Football_League_API.controller;

import com.example.Football_League_API.dto.response.ClassificacaoEntradaDto;
import com.example.Football_League_API.service.ClassificacaoService;
import com.example.Football_League_API.service.TemporadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Controller para consulta de tabelas de classificação.
 * Suporta visualizar a classificação de uma temporada específica ou de um campeonato.
 */
@RestController
@RequiredArgsConstructor
public class ClassificacaoController {

    private final ClassificacaoService service;
    private final TemporadaService temporadaService;

    /**
     * Retorna a tabela de classificação de uma temporada.
     * @param temporadaId ID da temporada
     * @return Lista de times ordenados por posição (com desempate aplicado)
     */
    @GetMapping("/temporadas/{temporadaId}/classificacao")
    public ResponseEntity<List<ClassificacaoEntradaDto>> getClassificacao(
            @PathVariable Long temporadaId) {
        List<ClassificacaoEntradaDto> classificacao = service.calcularClassificacao(temporadaId);
        return ResponseEntity.ok(classificacao);
    }

    /**
     * Retorna a tabela de classificação de um campeonato.
     * Por padrão, retorna a classificação da temporada em andamento.
     * Permite override via query param ?temporadaId= para escolher outra temporada.
     * @param campeonatoId ID do campeonato
     * @param temporadaId ID opcional da temporada (override)
     * @return Lista de times ordenados por posição
     */
    @GetMapping("/campeonatos/{campeonatoId}/classificacao")
    public ResponseEntity<List<ClassificacaoEntradaDto>> getClassificacaoCampeonato(
            @PathVariable Long campeonatoId,
            @RequestParam(required = false) Long temporadaId) {
        Long idTemporada = temporadaId != null ?
                temporadaId :
                temporadaService.findTemporadaAtualByCampeonato(campeonatoId);

        List<ClassificacaoEntradaDto> classificacao = service.calcularClassificacao(idTemporada);
        return ResponseEntity.ok(classificacao);
    }
}
