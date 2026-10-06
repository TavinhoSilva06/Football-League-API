package com.example.Football_League_API.controller;

import com.example.Football_League_API.dto.request.EstatisticaJogadorRequestDto;
import com.example.Football_League_API.dto.response.EstatisticaJogadorResponseDto;
import com.example.Football_League_API.service.EstatisticaJogadorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estatisticas")
public class EstatisticaJogadorController {

    private final EstatisticaJogadorService service;

    public EstatisticaJogadorController(EstatisticaJogadorService service) {
        this.service = service;
    }

    @GetMapping("/jogadores/{jogadorId}")
    public ResponseEntity<EstatisticaJogadorResponseDto> getByJogadorAndTemporada(
            @PathVariable Long jogadorId,
            @RequestParam Long temporadaId) {
        return ResponseEntity.ok(service.findByJogadorAndTemporada(jogadorId, temporadaId));
    }

    @PutMapping("/jogadores/{jogadorId}")
    public ResponseEntity<EstatisticaJogadorResponseDto> upsertEstatistica(
            @PathVariable Long jogadorId,
            @RequestParam Long temporadaId,
            @Valid @RequestBody EstatisticaJogadorRequestDto dto) {
        return ResponseEntity.ok(service.upsert(jogadorId, temporadaId, dto));
    }

    @GetMapping("/temporadas/{temporadaId}/artilharia")
    public ResponseEntity<List<EstatisticaJogadorResponseDto>> getArtilharia(
            @PathVariable Long temporadaId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.getArtilharia(temporadaId, limit));
    }

    @GetMapping("/temporadas/{temporadaId}/assistencias")
    public ResponseEntity<List<EstatisticaJogadorResponseDto>> getAssistencias(
            @PathVariable Long temporadaId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.getAssistencias(temporadaId, limit));
    }

    @GetMapping("/temporadas/{temporadaId}")
    public ResponseEntity<List<EstatisticaJogadorResponseDto>> getByTemporada(
            @PathVariable Long temporadaId) {
        return ResponseEntity.ok(service.getByTemporada(temporadaId));
    }
}
