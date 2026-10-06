package com.example.Football_League_API.controller;

import com.example.Football_League_API.dto.request.EventoPartidaRequestDto;
import com.example.Football_League_API.dto.response.EventoPartidaResponseDto;
import com.example.Football_League_API.service.EventoPartidaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partidas/{partidaId}/eventos")
public class EventoPartidaController {

    private final EventoPartidaService service;

    public EventoPartidaController(EventoPartidaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EventoPartidaResponseDto>> listarEventosPorPartida(@PathVariable Long partidaId) {
        return ResponseEntity.ok(service.findByPartida(partidaId));
    }

    @GetMapping("/{eventoId}")
    public ResponseEntity<EventoPartidaResponseDto> getEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(service.findById(eventoId));
    }

    @PostMapping
    public ResponseEntity<EventoPartidaResponseDto> criarEvento(
            @PathVariable Long partidaId,
            @Valid @RequestBody EventoPartidaRequestDto dto) {
        return ResponseEntity.status(201).body(service.create(partidaId, dto));
    }

    @DeleteMapping("/{eventoId}")
    public ResponseEntity<Void> deletarEvento(@PathVariable Long eventoId) {
        service.delete(eventoId);
        return ResponseEntity.noContent().build();
    }
}
