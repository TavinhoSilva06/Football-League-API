package com.example.Football_League_API.controller;

import com.example.Football_League_API.dto.request.TemporadaRequestDto;
import com.example.Football_League_API.dto.response.TemporadaResponseDto;
import com.example.Football_League_API.service.TemporadaService;
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
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TemporadaController {

    private final TemporadaService service;

    @GetMapping("/temporadas")
    public ResponseEntity<List<TemporadaResponseDto>> findAll() {
        List<TemporadaResponseDto> temporadas = service.findAll();
        return ResponseEntity.ok(temporadas);
    }

    @GetMapping("/campeonatos/{campeonatoId}/temporadas")
    public ResponseEntity<List<TemporadaResponseDto>> findByCampeonato(@PathVariable Long campeonatoId) {
        List<TemporadaResponseDto> temporadas = service.findByCampeonato(campeonatoId);
        return ResponseEntity.ok(temporadas);
    }

    @GetMapping("/temporadas/{id}")
    public ResponseEntity<TemporadaResponseDto> findById(@PathVariable Long id) {
        TemporadaResponseDto temporada = service.findById(id);
        return ResponseEntity.ok(temporada);
    }

    @PostMapping("/campeonatos/{campeonatoId}/temporadas")
    public ResponseEntity<TemporadaResponseDto> create(
            @PathVariable Long campeonatoId,
            @Valid @RequestBody TemporadaRequestDto dto) {
        TemporadaResponseDto created = service.create(campeonatoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/temporadas/{id}")
    public ResponseEntity<TemporadaResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody TemporadaRequestDto dto) {
        TemporadaResponseDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/temporadas/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/temporadas/{id}/encerrar")
    public ResponseEntity<TemporadaResponseDto> encerrar(@PathVariable Long id) {
        // Verifica se está completa e encerra
        TemporadaResponseDto encerrada = service.encerrar(id);
        return ResponseEntity.ok(encerrada);
    }
}
