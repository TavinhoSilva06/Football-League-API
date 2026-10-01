package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.request.TimeRequestDto;
import com.example.Football_League_API.dto.response.TimeResponseDto;
import com.example.Football_League_API.entity.Time;
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.mapper.TimeMapper;
import com.example.Football_League_API.repository.TimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeService {

    private final TimeRepository repository;
    private final TimeMapper mapper;

    public List<TimeResponseDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public TimeResponseDto findById(Long id) {
        Time entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Time não encontrado com id: " + id));
        return mapper.toResponseDto(entity);
    }

    /**
     * Busca todos os times que participam de um campeonato (em qualquer temporada).
     * Útil para listar elenco completo de um campeonato.
     */
    public List<TimeResponseDto> findByCampeonatoId(Long campeonatoId) {
        return repository.findByCampeonatoId(campeonatoId)
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public TimeResponseDto create(TimeRequestDto dto) {
        Time entity = mapper.toEntity(dto);
        Time saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    public TimeResponseDto update(Long id, TimeRequestDto dto) {
        Time entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Time não encontrado com id: " + id));
        mapper.updateEntity(dto, entity);
        Time updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        Time entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Time não encontrado com id: " + id));
        repository.delete(entity);
    }
}
