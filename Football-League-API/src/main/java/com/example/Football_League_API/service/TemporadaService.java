package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.request.TemporadaRequestDto;
import com.example.Football_League_API.dto.response.TemporadaResponseDto;
import com.example.Football_League_API.entity.Campeonato;
import com.example.Football_League_API.entity.Partida;
import com.example.Football_League_API.entity.Temporada;
import com.example.Football_League_API.enu.StatusPartida;
import com.example.Football_League_API.enu.StatusTemporada;
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.mapper.TemporadaMapper;
import com.example.Football_League_API.repository.CampeonatoRepository;
import com.example.Football_League_API.repository.PartidaRepository;
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
    private final PartidaRepository partidaRepository;
    private final TemporadaMapper mapper;

    /**
     * Busca todas as temporadas do sistema (independente de campeonato).
     * Útil para visão geral de todas as temporadas em execução.
     */
    public List<TemporadaResponseDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

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

    /**
     * Verifica se todas as rodadas da temporada foram completadas.
     * Uma rodada é completa quando todas as suas partidas foram finalizadas.
     *
     * @param temporadaId ID da temporada
     * @return true se todas as rodadas foram jogadas, false caso contrário
     */
    public boolean isCompleta(Long temporadaId) {
        Temporada temporada = repository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com id: " + temporadaId));

        // Se não tem numRodadas definido, considerar incompleta
        if (temporada.getNumRodadas() == null || temporada.getNumRodadas() == 0) {
            return false;
        }

        // Buscar a rodada máxima com partidas finalizadas
        List<Partida> partidas = temporada.getPartidas();

        if (partidas == null || partidas.isEmpty()) {
            return false; // Sem partidas, não está completa
        }

        // Encontrar a rodada máxima
        Integer rodadaMaxima = partidas.stream()
                .map(Partida::getRodada)
                .max(Integer::compareTo)
                .orElse(0);

        // Verificar se a rodada máxima é igual ao número de rodadas
        if (!rodadaMaxima.equals(temporada.getNumRodadas())) {
            return false; // Ainda faltam rodadas
        }

        // Verificar se todas as partidas até a última rodada estão finalizadas
        long partidasNaoFinalizadas = partidas.stream()
                .filter(p -> p.getRodada() <= temporada.getNumRodadas())
                .filter(p -> !StatusPartida.FINALIZADA.equals(p.getStatus()))
                .count();

        return partidasNaoFinalizadas == 0; // Completa se todas as partidas foram finalizadas
    }

    /**
     * Encerra uma temporada se todas as rodadas foram completadas.
     * Muda o status para ENCERRADA.
     *
     * @param temporadaId ID da temporada
     * @return TemporadaResponseDto com status atualizado
     * @throws IllegalArgumentException se a temporada ainda não está completa
     */
    public TemporadaResponseDto encerrar(Long temporadaId) {
        Temporada temporada = repository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com id: " + temporadaId));

        // Verificar se está completa
        if (!isCompleta(temporadaId)) {
            throw new IllegalArgumentException(
                "Temporada não pode ser encerrada. Ainda existem rodadas ou partidas não finalizadas."
            );
        }

        // Atualizar status para ENCERRADA
        temporada.setStatus(StatusTemporada.ENCERRADA);
        Temporada updated = repository.save(temporada);

        return mapper.toResponseDto(updated);
    }
}
