package com.example.Football_League_API.controller;

import com.example.Football_League_API.dto.request.PartidaRequestDto;
import com.example.Football_League_API.dto.response.PartidaResponseDto;
import com.example.Football_League_API.enu.StatusPartida;
import com.example.Football_League_API.service.PartidaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PartidaController {

    private final PartidaService service;

    @GetMapping("/partidas")
    public ResponseEntity<List<PartidaResponseDto>> findAll() {
        List<PartidaResponseDto> partidas = service.findAll();
        return ResponseEntity.ok(partidas);
    }

    @GetMapping("/partidas/{id}")
    public ResponseEntity<PartidaResponseDto> findById(@PathVariable Long id) {
        PartidaResponseDto partida = service.findById(id);
        return ResponseEntity.ok(partida);
    }

    @PostMapping("/temporadas/{temporadaId}/partidas")
    public ResponseEntity<PartidaResponseDto> create(
            @PathVariable Long temporadaId,
            @Valid @RequestBody PartidaRequestDto dto) {
        PartidaResponseDto created = service.create(temporadaId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/temporadas/{temporadaId}/partidas")
    public ResponseEntity<List<PartidaResponseDto>> findByTemporada(
            @PathVariable Long temporadaId,
            @RequestParam(required = false) Integer rodada,
            @RequestParam(required = false) StatusPartida status) {

        List<PartidaResponseDto> partidas;

        if (rodada != null && status != null) {
            partidas = service.findByTemporadaIdAndRodadaAndStatus(temporadaId, rodada, status);
        } else if (rodada != null) {
            partidas = service.findByTemporadaIdAndRodada(temporadaId, rodada);
        } else if (status != null) {
            partidas = service.findByTemporadaIdAndStatus(temporadaId, status);
        } else {
            partidas = service.findByTemporadaId(temporadaId);
        }

        return ResponseEntity.ok(partidas);
    }

    @GetMapping("/times/{timeId}/partidas")
    public ResponseEntity<List<PartidaResponseDto>> findByTime(
            @PathVariable Long timeId,
            @RequestParam(required = false) Long temporadaId) {

        List<PartidaResponseDto> partidas;

        if (temporadaId != null) {
            partidas = service.findByTimeIdAndTemporadaId(timeId, temporadaId);
        } else {
            partidas = service.findByTimeId(timeId);
        }

        return ResponseEntity.ok(partidas);
    }

    @PutMapping("/partidas/{id}")
    public ResponseEntity<PartidaResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody PartidaRequestDto dto) {
        PartidaResponseDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/partidas/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
