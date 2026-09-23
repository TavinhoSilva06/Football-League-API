package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.request.ParticipacaoRequestDto;
import com.example.Football_League_API.dto.response.ParticipacaoResponseDto;
import com.example.Football_League_API.entity.Participacao;
import com.example.Football_League_API.entity.Temporada;
import com.example.Football_League_API.entity.Time;
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.mapper.ParticipacaoMapper;
import com.example.Football_League_API.repository.ParticipacaoRepository;
import com.example.Football_League_API.repository.TemporadaRepository;
import com.example.Football_League_API.repository.TimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipacaoService {

    private final ParticipacaoRepository repository;
    private final TemporadaRepository temporadaRepository;
    private final TimeRepository timeRepository;
    private final ParticipacaoMapper mapper;

    public List<ParticipacaoResponseDto> findByTemporadaId(Long temporadaId) {
        temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com id: " + temporadaId));
        return repository.findByTemporadaId(temporadaId)
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public ParticipacaoResponseDto create(Long temporadaId, ParticipacaoRequestDto dto) {
        Temporada temporada = temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com id: " + temporadaId));

        Time time = timeRepository.findById(dto.getTimeId())
                .orElseThrow(() -> new EntityNotFoundException("Time não encontrado com id: " + dto.getTimeId()));

        // Verificar se já existe participacao
        if (repository.findByTemporadaIdAndTimeId(temporadaId, dto.getTimeId()).isPresent()) {
            throw new DataIntegrityViolationException("Este time já está participando desta temporada");
        }

        Participacao entity = Participacao.builder()
                .temporada(temporada)
                .time(time)
                .build();

        Participacao saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    public void delete(Long id) {
        Participacao entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Participação não encontrada com id: " + id));
        repository.delete(entity);
    }
}
