package com.example.Football_League_API.controller;

import com.example.Football_League_API.dto.response.ClassificacaoEntradaDto;
import com.example.Football_League_API.service.ClassificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Controller para consulta de tabelas de classificação.
 * Suporta visualizar a classificação de uma temporada específica.
 */
@RestController
@RequiredArgsConstructor
public class ClassificacaoController {

    private final ClassificacaoService service;

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
}
