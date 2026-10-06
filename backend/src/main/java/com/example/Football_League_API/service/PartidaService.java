package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.request.PartidaRequestDto;
import com.example.Football_League_API.dto.request.ResultadoRequestDto;
import com.example.Football_League_API.dto.response.PartidaResponseDto;
import com.example.Football_League_API.entity.EventoPartida;
import com.example.Football_League_API.entity.Partida;
import com.example.Football_League_API.entity.Temporada;
import com.example.Football_League_API.entity.Time;
import com.example.Football_League_API.enu.StatusPartida;
import com.example.Football_League_API.enu.TipoEvento;
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.mapper.PartidaMapper;
import com.example.Football_League_API.repository.EstatisticaJogadorRepository;
import com.example.Football_League_API.repository.EventoPartidaRepository;
import com.example.Football_League_API.repository.PartidaRepository;
import com.example.Football_League_API.repository.TemporadaRepository;
import com.example.Football_League_API.repository.TimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PartidaService {

    private final PartidaRepository repository;
    private final PartidaMapper mapper;
    private final TemporadaRepository temporadaRepository;
    private final TimeRepository timeRepository;
    private final EventoPartidaRepository eventoRepository;
    private final EstatisticaJogadorRepository estatisticaRepository;

    public PartidaResponseDto create(Long temporadaId, PartidaRequestDto dto) {
        validatePartidaCreation(temporadaId, dto);

        Temporada temporada = temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com ID: " + temporadaId));
        Time timeMandante = timeRepository.findById(dto.getTimeMandanteId())
                .orElseThrow(() -> new EntityNotFoundException("Time não encontrado com ID: " + dto.getTimeMandanteId()));
        Time timeVisitante = timeRepository.findById(dto.getTimeVisitanteId())
                .orElseThrow(() -> new EntityNotFoundException("Time não encontrado com ID: " + dto.getTimeVisitanteId()));

        Partida partida = mapper.toEntity(dto);
        partida.setTemporada(temporada);
        partida.setTimeMandante(timeMandante);
        partida.setTimeVisitante(timeVisitante);

        if (partida.getStatus() == null) {
            partida.setStatus(StatusPartida.AGENDADA);
        }

        Partida saved = repository.save(partida);
        return mapper.toResponseDto(saved);
    }

    public List<PartidaResponseDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public PartidaResponseDto findById(Long id) {
        Partida partida = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com ID: " + id));
        return mapper.toResponseDto(partida);
    }

    public List<PartidaResponseDto> findByTemporadaId(Long temporadaId) {
        temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com ID: " + temporadaId));
        return repository.findByTemporadaId(temporadaId)
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<PartidaResponseDto> findByTemporadaIdAndRodada(Long temporadaId, Integer rodada) {
        temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com ID: " + temporadaId));
        return repository.findByTemporadaIdAndRodada(temporadaId, rodada)
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<PartidaResponseDto> findByTemporadaIdAndStatus(Long temporadaId, StatusPartida status) {
        temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com ID: " + temporadaId));
        return repository.findByTemporadaIdAndStatus(temporadaId, status)
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<PartidaResponseDto> findByTemporadaIdAndRodadaAndStatus(Long temporadaId, Integer rodada, StatusPartida status) {
        temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com ID: " + temporadaId));
        return repository.findByTemporadaIdAndRodadaAndStatus(temporadaId, rodada, status)
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<PartidaResponseDto> findByTimeId(Long timeId) {
        timeRepository.findById(timeId)
                .orElseThrow(() -> new EntityNotFoundException("Time não encontrado com ID: " + timeId));
        return repository.findByTimeMandanteIdOrTimeVisitanteId(timeId, timeId)
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<PartidaResponseDto> findByTimeIdAndTemporadaId(Long timeId, Long temporadaId) {
        timeRepository.findById(timeId)
                .orElseThrow(() -> new EntityNotFoundException("Time não encontrado com ID: " + timeId));
        temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com ID: " + temporadaId));

        List<Partida> mandante = repository.findByTimeMandanteIdAndTemporadaId(timeId, temporadaId);
        List<Partida> visitante = repository.findByTimeVisitanteIdAndTemporadaId(timeId, temporadaId);

        return Stream.concat(mandante.stream(), visitante.stream())
                .distinct()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public PartidaResponseDto update(Long id, PartidaRequestDto dto) {
        Partida partida = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com ID: " + id));

        if (dto.getRodada() != null) {
            partida.setRodada(dto.getRodada());
        }

        if (dto.getDataHora() != null) {
            partida.setDataHora(dto.getDataHora());
        }

        if (dto.getGolsMandante() != null) {
            partida.setGolsMandante(dto.getGolsMandante());
        }

        if (dto.getGolsVisitante() != null) {
            partida.setGolsVisitante(dto.getGolsVisitante());
        }

        if (dto.getStatus() != null) {
            partida.setStatus(dto.getStatus());
        }

        if (dto.getLocal() != null) {
            partida.setLocal(dto.getLocal());
        }

        Partida updated = repository.save(partida);
        return mapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        Partida partida = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com ID: " + id));
        repository.delete(partida);
    }

    private void validatePartidaCreation(Long temporadaId, PartidaRequestDto dto) {
        // Validar que mandante != visitante
        if (dto.getTimeMandanteId().equals(dto.getTimeVisitanteId())) {
            throw new IllegalArgumentException("Time mandante não pode ser igual ao time visitante");
        }

        // Validar que temporada existe e obter seus dados
        Temporada temporada = temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com ID: " + temporadaId));

        // Validar que times existem
        timeRepository.findById(dto.getTimeMandanteId())
                .orElseThrow(() -> new EntityNotFoundException("Time não encontrado com ID: " + dto.getTimeMandanteId()));
        timeRepository.findById(dto.getTimeVisitanteId())
                .orElseThrow(() -> new EntityNotFoundException("Time não encontrado com ID: " + dto.getTimeVisitanteId()));

        // Validar que a rodada não ultrapassa o número máximo de rodadas
        if (dto.getRodada() != null && temporada.getNumRodadas() != null) {
            if (dto.getRodada() > temporada.getNumRodadas()) {
                throw new IllegalArgumentException(
                    "Rodada " + dto.getRodada() + " não pode ser criada. " +
                    "Temporada tem apenas " + temporada.getNumRodadas() + " rodadas."
                );
            }
            if (dto.getRodada() < 1) {
                throw new IllegalArgumentException("Rodada deve ser maior que 0");
            }
        }
    }

    public PartidaResponseDto registrarResultado(Long id, ResultadoRequestDto dto) {
        Partida partida = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com ID: " + id));

        // Buscar todos os eventos de golo registrados para esta partida
        List<EventoPartida> eventos = eventoRepository.findByPartidaId(id);

        int golosMandanteEvento = (int) eventos.stream()
                .filter(e -> e.getTipo() == TipoEvento.GOLO &&
                        e.getJogador().getTime().getId().equals(partida.getTimeMandante().getId()))
                .count();

        int golosVisitanteEvento = (int) eventos.stream()
                .filter(e -> e.getTipo() == TipoEvento.GOLO &&
                        e.getJogador().getTime().getId().equals(partida.getTimeVisitante().getId()))
                .count();

        // Validar se o placar informado bate com os eventos registrados
        if (dto.getGolsMandante() != null && !dto.getGolsMandante().equals(golosMandanteEvento)) {
            throw new IllegalArgumentException(
                "Placar do mandante (" + dto.getGolsMandante() + ") não corresponde aos eventos registrados (" + golosMandanteEvento + " gols)"
            );
        }

        if (dto.getGolsVisitante() != null && !dto.getGolsVisitante().equals(golosVisitanteEvento)) {
            throw new IllegalArgumentException(
                "Placar do visitante (" + dto.getGolsVisitante() + ") não corresponde aos eventos registrados (" + golosVisitanteEvento + " gols)"
            );
        }

        // Atualizar com os gols dos eventos (ou valores informados se validarem)
        partida.setGolsMandante(dto.getGolsMandante() != null ? dto.getGolsMandante() : golosMandanteEvento);
        partida.setGolsVisitante(dto.getGolsVisitante() != null ? dto.getGolsVisitante() : golosVisitanteEvento);
        partida.setStatus(StatusPartida.FINALIZADA);

        Partida updated = repository.save(partida);

        // Incrementar número de jogos para todos os jogadores que participaram
        atualizarNumeroJogos(partida, eventos);

        return mapper.toResponseDto(updated);
    }

    private void atualizarNumeroJogos(Partida partida, List<EventoPartida> eventos) {
        // Obter todos os jogadores únicos que participaram (tiveram eventos)
        eventos.stream()
                .map(EventoPartida::getJogador)
                .distinct()
                .forEach(jogador -> {
                    var stats = estatisticaRepository
                            .findByJogadorIdAndTemporadaId(jogador.getId(), partida.getTemporada().getId())
                            .orElse(null);

                    if (stats != null) {
                        stats.setJogos(stats.getJogos() + 1);
                        estatisticaRepository.save(stats);
                    }
                });
    }

    protected Partida findByIdEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com ID: " + id));
    }
}
