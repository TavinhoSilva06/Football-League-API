package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.request.TemporadaRequestDto;
import com.example.Football_League_API.dto.response.TemporadaResponseDto;
import com.example.Football_League_API.entity.Campeonato;
import com.example.Football_League_API.entity.Temporada;
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.mapper.TemporadaMapper;
import com.example.Football_League_API.repository.CampeonatoRepository;
import com.example.Football_League_API.repository.TemporadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemporadaService {

    private final TemporadaRepository repository;
    private final CampeonatoRepository campeonatoRepository;
    private final TemporadaMapper mapper;

    public List<TemporadaResponseDto> findByCampeonato(Long campeonatoId) {
        campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado com id: " + campeonatoId));
        return repository.findByCampeonatoId(campeonatoId)
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public TemporadaResponseDto findById(Long id) {
        Temporada entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com id: " + id));
        return mapper.toResponseDto(entity);
    }

    public TemporadaResponseDto create(Long campeonatoId, TemporadaRequestDto dto) {
        Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado com id: " + campeonatoId));
        Temporada entity = mapper.toEntity(dto, campeonato);
        Temporada saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    public TemporadaResponseDto update(Long id, TemporadaRequestDto dto) {
        Temporada entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com id: " + id));
        mapper.updateEntity(dto, entity);
        Temporada updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        Temporada entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com id: " + id));
        repository.delete(entity);
    }
}
