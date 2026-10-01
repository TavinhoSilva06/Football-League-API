package com.example.Football_League_API.controller;

import com.example.Football_League_API.dto.request.CampeonatoRequestDto;
import com.example.Football_League_API.dto.response.CampeonatoResponseDto;
import com.example.Football_League_API.dto.response.TimeResponseDto;
import com.example.Football_League_API.service.CampeonatoService;
import com.example.Football_League_API.service.TimeService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/campeonatos")
@RequiredArgsConstructor
public class CampeonatoController {

    private final CampeonatoService service;
    private final TimeService timeService;

    @GetMapping
    public ResponseEntity<List<CampeonatoResponseDto>> findAll() {
        List<CampeonatoResponseDto> campeonatos = service.findAll();
        return ResponseEntity.ok(campeonatos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampeonatoResponseDto> findById(@PathVariable Long id) {
        CampeonatoResponseDto campeonato = service.findById(id);
        return ResponseEntity.ok(campeonato);
    }

    @GetMapping("/{id}/times")
    public ResponseEntity<List<TimeResponseDto>> findTimesByCampeonato(@PathVariable Long id) {
        // Valida se campeonato existe
        service.findById(id);
        // Busca todos os times que participam deste campeonato
        List<TimeResponseDto> times = timeService.findByCampeonatoId(id);
        return ResponseEntity.ok(times);
    }

    @PostMapping
    public ResponseEntity<CampeonatoResponseDto> create(@Valid @RequestBody CampeonatoRequestDto dto) {
        CampeonatoResponseDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampeonatoResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CampeonatoRequestDto dto) {
        CampeonatoResponseDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
