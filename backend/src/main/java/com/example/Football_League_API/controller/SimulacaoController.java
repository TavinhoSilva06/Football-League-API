package com.example.Football_League_API.controller;

import com.example.Football_League_API.service.SimulacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/temporadas/{temporadaId}")
@RequiredArgsConstructor
public class SimulacaoController {

    private final SimulacaoService service;

    @PostMapping("/calendario")
    public ResponseEntity<Map<String, Integer>> gerarCalendario(@PathVariable Long temporadaId) {
        return ResponseEntity.ok(Map.of("partidasCriadas", service.gerarCalendario(temporadaId)));
    }

    @PostMapping("/rodadas/{rodada}/simular")
    public ResponseEntity<Map<String, Integer>> simularRodada(@PathVariable Long temporadaId,
                                                              @PathVariable Integer rodada) {
        return ResponseEntity.ok(Map.of("partidasSimuladas", service.simularRodada(temporadaId, rodada)));
    }
}
