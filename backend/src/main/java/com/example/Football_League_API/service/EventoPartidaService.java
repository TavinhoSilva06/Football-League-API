package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.request.EventoPartidaRequestDto;
import com.example.Football_League_API.dto.response.EventoPartidaResponseDto;
import com.example.Football_League_API.entity.EventoPartida;
import com.example.Football_League_API.entity.Jogador;
import com.example.Football_League_API.entity.Partida;
import com.example.Football_League_API.entity.Temporada;
import com.example.Football_League_API.entity.EstatisticaJogador;
import com.example.Football_League_API.enu.TipoEvento;
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.mapper.EventoPartidaMapper;
import com.example.Football_League_API.repository.EventoPartidaRepository;
import com.example.Football_League_API.repository.JogadorRepository;
import com.example.Football_League_API.repository.PartidaRepository;
import com.example.Football_League_API.repository.TemporadaRepository;
import com.example.Football_League_API.repository.EstatisticaJogadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoPartidaService {

    private final EventoPartidaRepository eventoRepository;
    private final PartidaRepository partidaRepository;
    private final JogadorRepository jogadorRepository;
    private final TemporadaRepository temporadaRepository;
    private final EstatisticaJogadorRepository estatisticaRepository;
    private final EventoPartidaMapper mapper;

    public EventoPartidaService(EventoPartidaRepository eventoRepository,
                               PartidaRepository partidaRepository,
                               JogadorRepository jogadorRepository,
                               TemporadaRepository temporadaRepository,
                               EstatisticaJogadorRepository estatisticaRepository,
                               EventoPartidaMapper mapper) {
        this.eventoRepository = eventoRepository;
        this.partidaRepository = partidaRepository;
        this.jogadorRepository = jogadorRepository;
        this.temporadaRepository = temporadaRepository;
        this.estatisticaRepository = estatisticaRepository;
        this.mapper = mapper;
    }

    @Transactional
    public EventoPartidaResponseDto create(Long partidaId, EventoPartidaRequestDto dto) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com ID: " + partidaId));

        Jogador jogador = jogadorRepository.findById(dto.getJogadorId())
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado com ID: " + dto.getJogadorId()));

        Temporada temporada = partida.getTemporada();

        EventoPartida evento = EventoPartida.builder()
                .partida(partida)
                .jogador(jogador)
                .temporada(temporada)
                .tipo(dto.getTipo())
                .minuto(dto.getMinuto())
                .descricao(dto.getDescricao())
                .build();

        EventoPartida saved = eventoRepository.save(evento);
        atualizarEstatisticas(saved);

        return mapper.toResponseDto(saved);
    }

    public EventoPartidaResponseDto findById(Long id) {
        EventoPartida evento = eventoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado com ID: " + id));
        return mapper.toResponseDto(evento);
    }

    public List<EventoPartidaResponseDto> findByPartida(Long partidaId) {
        return eventoRepository.findByPartidaId(partidaId)
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<EventoPartidaResponseDto> findByJogadorAndTemporada(Long jogadorId, Long temporadaId) {
        return eventoRepository.findByJogadorIdAndTemporadaId(jogadorId, temporadaId)
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        EventoPartida evento = eventoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado com ID: " + id));
        eventoRepository.delete(evento);
        removerEstatisticas(evento);
    }

    @Transactional
    public void deletarEventosPorPartida(Long partidaId) {
        List<EventoPartida> eventos = eventoRepository.findByPartidaId(partidaId);
        for (EventoPartida evento : eventos) {
            removerEstatisticas(evento);
        }
        eventoRepository.deleteAll(eventos);
    }

    private void atualizarEstatisticas(EventoPartida evento) {
        EstatisticaJogador stats = obterOuCriarEstatistica(evento.getJogador().getId(), evento.getTemporada().getId());

        switch (evento.getTipo()) {
            case GOLO:
                stats.setGols(stats.getGols() + 1);
                break;
            case ASSISTENCIA:
                stats.setAssistencias(stats.getAssistencias() + 1);
                break;
            case CARTAO_AMARELO:
                stats.setCartoesAmarelos(stats.getCartoesAmarelos() + 1);
                break;
            case CARTAO_VERMELHO:
                stats.setCartoesVermelhos(stats.getCartoesVermelhos() + 1);
                break;
        }

        estatisticaRepository.save(stats);
    }

    private void removerEstatisticas(EventoPartida evento) {
        EstatisticaJogador stats = estatisticaRepository
                .findByJogadorIdAndTemporadaId(evento.getJogador().getId(), evento.getTemporada().getId())
                .orElse(null);

        if (stats == null) return;

        switch (evento.getTipo()) {
            case GOLO:
                stats.setGols(Math.max(0, stats.getGols() - 1));
                break;
            case ASSISTENCIA:
                stats.setAssistencias(Math.max(0, stats.getAssistencias() - 1));
                break;
            case CARTAO_AMARELO:
                stats.setCartoesAmarelos(Math.max(0, stats.getCartoesAmarelos() - 1));
                break;
            case CARTAO_VERMELHO:
                stats.setCartoesVermelhos(Math.max(0, stats.getCartoesVermelhos() - 1));
                break;
        }

        estatisticaRepository.save(stats);
    }

    private EstatisticaJogador obterOuCriarEstatistica(Long jogadorId, Long temporadaId) {
        return estatisticaRepository
                .findByJogadorIdAndTemporadaId(jogadorId, temporadaId)
                .orElseGet(() -> {
                    Jogador jogador = jogadorRepository.findById(jogadorId).orElse(null);
                    Temporada temporada = temporadaRepository.findById(temporadaId).orElse(null);
                    return EstatisticaJogador.builder()
                            .jogador(jogador)
                            .temporada(temporada)
                            .jogos(0)
                            .gols(0)
                            .assistencias(0)
                            .cartoesAmarelos(0)
                            .cartoesVermelhos(0)
                            .build();
                });
    }
}
