package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.request.EstatisticaJogadorRequestDto;
import com.example.Football_League_API.dto.response.EstatisticaJogadorResponseDto;
import com.example.Football_League_API.entity.EstatisticaJogador;
import com.example.Football_League_API.entity.Jogador;
import com.example.Football_League_API.entity.Temporada;
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.mapper.EstatisticaJogadorMapper;
import com.example.Football_League_API.repository.EstatisticaJogadorRepository;
import com.example.Football_League_API.repository.JogadorRepository;
import com.example.Football_League_API.repository.TemporadaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstatisticaJogadorService {

    private static final int MAX_LIMITE = 100;

    private final EstatisticaJogadorRepository estatisticaRepository;
    private final JogadorRepository jogadorRepository;
    private final TemporadaRepository temporadaRepository;
    private final EstatisticaJogadorMapper mapper;

    public EstatisticaJogadorService(EstatisticaJogadorRepository estatisticaRepository,
                                     JogadorRepository jogadorRepository,
                                     TemporadaRepository temporadaRepository,
                                     EstatisticaJogadorMapper mapper) {
        this.estatisticaRepository = estatisticaRepository;
        this.jogadorRepository = jogadorRepository;
        this.temporadaRepository = temporadaRepository;
        this.mapper = mapper;
    }

    @Transactional
    public EstatisticaJogadorResponseDto upsert(Long jogadorId, Long temporadaId, EstatisticaJogadorRequestDto dto) {
        Jogador jogador = jogadorRepository.findById(jogadorId)
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado com ID: " + jogadorId));

        Temporada temporada = temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com ID: " + temporadaId));

        EstatisticaJogador estatistica = estatisticaRepository
                .findByJogadorIdAndTemporadaId(jogadorId, temporadaId)
                .orElse(EstatisticaJogador.builder()
                        .jogador(jogador)
                        .temporada(temporada)
                        .build());

        mapper.updateEntityFromDto(dto, estatistica);
        EstatisticaJogador saved = estatisticaRepository.save(estatistica);
        return mapper.toResponseDto(saved);
    }

    public EstatisticaJogadorResponseDto findByJogadorAndTemporada(Long jogadorId, Long temporadaId) {
        EstatisticaJogador estatistica = estatisticaRepository
                .findByJogadorIdAndTemporadaId(jogadorId, temporadaId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Estatística não encontrada para jogador " + jogadorId + " na temporada " + temporadaId));
        return mapper.toResponseDto(estatistica);
    }

    public List<EstatisticaJogadorResponseDto> getArtilharia(Long temporadaId, int limit) {
        return estatisticaRepository.findArtilharia(temporadaId, pagina(limit))
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    public List<EstatisticaJogadorResponseDto> getAssistencias(Long temporadaId, int limit) {
        return estatisticaRepository.findAssistencias(temporadaId, pagina(limit))
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    public List<EstatisticaJogadorResponseDto> getCartoes(Long temporadaId, int limit) {
        return estatisticaRepository.findCartoes(temporadaId, pagina(limit))
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    public List<EstatisticaJogadorResponseDto> getByTemporada(Long temporadaId) {
        return estatisticaRepository.findByTemporadaIdOrderByGolsDesc(temporadaId)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    private Pageable pagina(int limit) {
        return PageRequest.of(0, Math.min(Math.max(limit, 1), MAX_LIMITE));
    }
}
