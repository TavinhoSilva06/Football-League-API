package com.example.Football_League_API.controller;

import com.example.Football_League_API.dto.request.JogadorRequestDto;
import com.example.Football_League_API.dto.response.JogadorResponseDto;
import com.example.Football_League_API.service.JogadorService;
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
@RequestMapping("/jogadores")
@RequiredArgsConstructor
public class JogadorController {

    private final JogadorService service;

    @GetMapping("/{id}")
    public ResponseEntity<JogadorResponseDto> findById(@PathVariable Long id) {
        JogadorResponseDto jogador = service.findById(id);
        return ResponseEntity.ok(jogador);
    }

    @GetMapping("/time/{timeId}")
    public ResponseEntity<List<JogadorResponseDto>> findByTimeId(@PathVariable Long timeId) {
        List<JogadorResponseDto> jogadores = service.findByTimeId(timeId);
        return ResponseEntity.ok(jogadores);
    }

    @PostMapping
    public ResponseEntity<JogadorResponseDto> create(@Valid @RequestBody JogadorRequestDto dto) {
        JogadorResponseDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JogadorResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody JogadorRequestDto dto) {
        JogadorResponseDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
