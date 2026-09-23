package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.request.JogadorRequestDto;
import com.example.Football_League_API.dto.response.JogadorResponseDto;
import com.example.Football_League_API.entity.Jogador;
import com.example.Football_League_API.entity.Time;
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.mapper.JogadorMapper;
import com.example.Football_League_API.repository.JogadorRepository;
import com.example.Football_League_API.repository.TimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JogadorService {

    private final JogadorRepository repository;
    private final TimeRepository timeRepository;
    private final JogadorMapper mapper;

    public JogadorResponseDto findById(Long id) {
        Jogador entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado com id: " + id));
        return mapper.toResponseDto(entity);
    }

    public List<JogadorResponseDto> findByTimeId(Long timeId) {
        timeRepository.findById(timeId)
                .orElseThrow(() -> new EntityNotFoundException("Time não encontrado com id: " + timeId));
        return repository.findByTimeId(timeId)
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public JogadorResponseDto create(JogadorRequestDto dto) {
        Time time = null;
        if (dto.getTimeId() != null) {
            time = timeRepository.findById(dto.getTimeId())
                    .orElseThrow(() -> new EntityNotFoundException("Time não encontrado com id: " + dto.getTimeId()));
        }
        Jogador entity = mapper.toEntity(dto, time);
        Jogador saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    public JogadorResponseDto update(Long id, JogadorRequestDto dto) {
        Jogador entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado com id: " + id));

        Time time = null;
        if (dto.getTimeId() != null) {
            time = timeRepository.findById(dto.getTimeId())
                    .orElseThrow(() -> new EntityNotFoundException("Time não encontrado com id: " + dto.getTimeId()));
        }

        mapper.updateEntity(dto, entity, time);
        Jogador updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        Jogador entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado com id: " + id));
        repository.delete(entity);
    }
}
