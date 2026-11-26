package com.example.Football_League_API.controller;

import com.example.Football_League_API.dto.request.PalpiteLoteRequestDto;
import com.example.Football_League_API.dto.request.PalpiteRequestDto;
import com.example.Football_League_API.dto.response.PalpiteResponseDto;
import com.example.Football_League_API.dto.response.RodadaPalpitesResponseDto;
import com.example.Football_League_API.service.PalpiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PalpiteController {

    private final PalpiteService service;

    @GetMapping("/temporadas/{temporadaId}/palpites/rodada-atual")
    public ResponseEntity<RodadaPalpitesResponseDto> rodadaAtual(@PathVariable Long temporadaId) {
        return ResponseEntity.ok(service.listarRodadaAtual(temporadaId));
    }

    @GetMapping("/temporadas/{temporadaId}/palpites/resultados")
    public ResponseEntity<List<RodadaPalpitesResponseDto>> resultados(@PathVariable Long temporadaId) {
        return ResponseEntity.ok(service.listarResultados(temporadaId));
    }

    @PostMapping("/palpites")
    public ResponseEntity<PalpiteResponseDto> salvar(@Valid @RequestBody PalpiteRequestDto dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @PostMapping("/palpites/lote")
    public ResponseEntity<List<PalpiteResponseDto>> salvarLote(@Valid @RequestBody PalpiteLoteRequestDto dto) {
        return ResponseEntity.ok(service.salvarLote(dto.getPalpites()));
    }
}
