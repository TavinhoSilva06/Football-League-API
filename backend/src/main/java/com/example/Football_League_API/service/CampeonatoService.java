package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.request.CampeonatoRequestDto;
import com.example.Football_League_API.dto.response.CampeonatoResponseDto;
import com.example.Football_League_API.entity.Campeonato;
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.mapper.CampeonatoMapper;
import com.example.Football_League_API.repository.CampeonatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampeonatoService {

    private final CampeonatoRepository repository;
    private final CampeonatoMapper mapper;

    public List<CampeonatoResponseDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public CampeonatoResponseDto findById(Long id) {
        Campeonato entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado com id: " + id));
        return mapper.toResponseDto(entity);
    }

    public CampeonatoResponseDto create(CampeonatoRequestDto dto) {
        Campeonato entity = mapper.toEntity(dto);
        Campeonato saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    public CampeonatoResponseDto update(Long id, CampeonatoRequestDto dto) {
        Campeonato entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado com id: " + id));
        mapper.updateEntity(dto, entity);
        Campeonato updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        Campeonato entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado com id: " + id));
        repository.delete(entity);
    }
}
