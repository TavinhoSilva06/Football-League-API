package com.example.Football_League_API.controller;

import com.example.Football_League_API.dto.request.ParticipacaoRequestDto;
import com.example.Football_League_API.dto.response.ParticipacaoResponseDto;
import com.example.Football_League_API.service.ParticipacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ParticipacaoController {

    private final ParticipacaoService service;

    @GetMapping("/temporadas/{temporadaId}/participacoes")
    public ResponseEntity<List<ParticipacaoResponseDto>> findByTemporadaId(@PathVariable Long temporadaId) {
        List<ParticipacaoResponseDto> participacoes = service.findByTemporadaId(temporadaId);
        return ResponseEntity.ok(participacoes);
    }

    @PostMapping("/temporadas/{temporadaId}/participacoes")
    public ResponseEntity<ParticipacaoResponseDto> create(
            @PathVariable Long temporadaId,
            @Valid @RequestBody ParticipacaoRequestDto dto) {
        ParticipacaoResponseDto created = service.create(temporadaId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/participacoes/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
