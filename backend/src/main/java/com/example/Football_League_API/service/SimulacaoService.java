package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.response.SimulacaoRodadaResponseDto;
import com.example.Football_League_API.entity.EstatisticaJogador;
import com.example.Football_League_API.entity.EventoPartida;
import com.example.Football_League_API.entity.Jogador;
import com.example.Football_League_API.entity.Participacao;
import com.example.Football_League_API.entity.Partida;
import com.example.Football_League_API.entity.Temporada;
import com.example.Football_League_API.entity.Time;
import com.example.Football_League_API.enu.PosicaoJogador;
import com.example.Football_League_API.enu.StatusPartida;
import com.example.Football_League_API.enu.TipoEvento;
import com.example.Football_League_API.event.PrazoPalpiteDefinidoEvent;
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.repository.EstatisticaJogadorRepository;
import com.example.Football_League_API.repository.EventoPartidaRepository;
import com.example.Football_League_API.repository.JogadorRepository;
import com.example.Football_League_API.repository.ParticipacaoRepository;
import com.example.Football_League_API.repository.PartidaRepository;
import com.example.Football_League_API.repository.TemporadaRepository;
import com.example.Football_League_API.repository.TimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Gera o calendário de turno e returno de uma temporada e simula as partidas,
 * sorteando placar, gols, assistências, cartões e atualizando as estatísticas dos jogadores.
 */
@Service
@RequiredArgsConstructor
public class SimulacaoService {

    private static final int TITULARES = 11;
    private static final double MEDIA_GOLS_POR_TIME = 1.3;
    private static final int MAX_GOLS_POR_TIME = 6;
    private static final double CHANCE_ASSISTENCIA = 0.7;
    private static final int MAX_AMARELOS_POR_TIME = 4;
    private static final double CHANCE_VERMELHO_POR_TIME = 0.05;
    private static final int MINUTO_FINAL = 90;
    private static final int HORA_PARTIDA = 16;

    private final TemporadaRepository temporadaRepository;
    private final TimeRepository timeRepository;
    private final JogadorRepository jogadorRepository;
    private final ParticipacaoRepository participacaoRepository;
    private final PartidaRepository partidaRepository;
    private final EventoPartidaRepository eventoRepository;
    private final EstatisticaJogadorRepository estatisticaRepository;
    private final PalpiteService palpiteService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${palpite.prazo-minutos:4}")
    private int prazoMinutos;

    private final Random random = new Random();

    // Par de times de uma rodada do primeiro turno (mandante x visitante)
    private record Confronto(int rodada, Time mandante, Time visitante) {}

    private record EstatisticaKey(Long jogadorId, Long temporadaId) {}

    /**
     * Inscreve os times com jogadores na temporada e cria as partidas de turno e returno,
     * todas agendadas. Cada time enfrenta os demais duas vezes, com mando invertido no returno.
     */
    @Transactional
    public int gerarCalendario(Long temporadaId) {
        Temporada temporada = buscarTemporada(temporadaId);

        if (!partidaRepository.findByTemporadaId(temporadaId).isEmpty()) {
            throw new IllegalArgumentException("O calendário desta temporada já foi gerado");
        }

        // Só entram times com jogadores; times sem elenco não têm quem sortear para gols e cartões
        List<Time> times = timeRepository.findAll().stream()
                .filter(time -> !jogadorRepository.findByTimeId(time.getId()).isEmpty())
                .sorted(Comparator.comparing(Time::getNome))
                .toList();

        if (times.size() < 2 || times.size() % 2 != 0) {
            throw new IllegalArgumentException(
                    "É preciso um número par de times com jogadores. Encontrados: " + times.size());
        }

        for (Time time : times) {
            if (participacaoRepository.findByTemporadaIdAndTimeId(temporadaId, time.getId()).isEmpty()) {
                participacaoRepository.save(Participacao.builder().temporada(temporada).time(time).build());
            }
        }

        int rodadasPorTurno = times.size() - 1;
        int totalRodadas = rodadasPorTurno * 2;

        List<Partida> partidas = new ArrayList<>();
        for (Confronto confronto : montarConfrontosDoTurno(times)) {
            partidas.add(criarPartida(temporada, confronto.rodada(), confronto.mandante(), confronto.visitante(), totalRodadas));
            // Returno: mesma dupla, com mando trocado, na rodada seguinte ao fim do turno
            partidas.add(criarPartida(temporada, confronto.rodada() + rodadasPorTurno, confronto.visitante(), confronto.mandante(), totalRodadas));
        }

        LocalDateTime limite = LocalDateTime.now().plusMinutes(prazoMinutos);
        partidas.stream()
                .filter(partida -> partida.getRodada() == 1)
                .forEach(partida -> partida.setDataLimitePalpite(limite));
        partidaRepository.saveAll(partidas);
        eventPublisher.publishEvent(new PrazoPalpiteDefinidoEvent(temporadaId, 1, limite));
        return partidas.size();
    }

    /**
     * Simula todas as partidas agendadas de uma rodada: placar, eventos e estatísticas.
     */
    @Transactional
    public synchronized SimulacaoRodadaResponseDto simularRodada(Long temporadaId, Integer rodada) {
        Temporada temporada = buscarTemporada(temporadaId);

        List<Partida> partidas = partidaRepository.findByTemporadaIdAndRodadaAndStatus(
                temporadaId, rodada, StatusPartida.AGENDADA);

        if (partidas.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma partida agendada na rodada " + rodada);
        }

        LocalDateTime limite = partidas.get(0).getDataLimitePalpite();
        if (limite == null || LocalDateTime.now().isBefore(limite)) {
            throw new IllegalArgumentException("A simulação começa após o prazo de palpites da rodada " + rodada);
        }

        Map<Long, List<Jogador>> elencosPorTime = carregarElencos(partidas);
        Map<EstatisticaKey, EstatisticaJogador> estatisticas =
                carregarEstatisticas(temporada.getId(), elencosPorTime);
        List<EventoPartida> eventos = new ArrayList<>();
        for (Partida partida : partidas) {
            simularPartida(partida, temporada, elencosPorTime, estatisticas, eventos);
        }

        partidaRepository.saveAll(partidas);
        eventoRepository.saveAll(eventos);
        estatisticaRepository.saveAll(estatisticas.values());
        PalpiteService.AvaliacaoRodada avaliacao = palpiteService.avaliarRodada(temporadaId, rodada);
        abrirProximaRodada(temporada.getId(), rodada);
        return SimulacaoRodadaResponseDto.builder()
                .partidasSimuladas(partidas.size())
                .palpitesAvaliados(avaliacao.palpitesAvaliados())
                .pontosDistribuidos(avaliacao.pontosDistribuidos())
                .build();
    }

    private Map<Long, List<Jogador>> carregarElencos(List<Partida> partidas) {
        Set<Long> timesIds = partidas.stream()
                .flatMap(partida -> java.util.stream.Stream.of(
                        partida.getTimeMandante(), partida.getTimeVisitante()))
                .filter(Objects::nonNull)
                .map(Time::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, List<Jogador>> elencosPorTime = new HashMap<>();
        for (Long timeId : timesIds) {
            elencosPorTime.put(timeId, jogadorRepository.findByTimeId(timeId));
        }
        return elencosPorTime;
    }

    private Map<EstatisticaKey, EstatisticaJogador> carregarEstatisticas(
            Long temporadaId, Map<Long, List<Jogador>> elencosPorTime) {
        Set<Long> jogadoresIds = elencosPorTime.values().stream()
                .flatMap(List::stream)
                .map(Jogador::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<EstatisticaKey, EstatisticaJogador> estatisticas = new HashMap<>();
        if (jogadoresIds.isEmpty()) {
            return estatisticas;
        }

        estatisticaRepository.findByTemporadaIdAndJogadorIdIn(temporadaId, jogadoresIds)
                .forEach(estatistica -> estatisticas.put(
                        new EstatisticaKey(estatistica.getJogador().getId(), temporadaId), estatistica));
        return estatisticas;
    }

    private void abrirProximaRodada(Long temporadaId, Integer rodada) {
        Optional<Partida> proxima = partidaRepository.findFirstByTemporadaIdAndStatusOrderByRodadaAsc(
                temporadaId, StatusPartida.AGENDADA);
        if (proxima.isEmpty() || !Objects.equals(proxima.get().getRodada(), rodada + 1)) {
            return;
        }

        LocalDateTime limite = LocalDateTime.now().plusMinutes(prazoMinutos);
        List<Partida> partidas = partidaRepository.findByTemporadaIdAndRodada(temporadaId, rodada + 1);
        partidas.forEach(partida -> partida.setDataLimitePalpite(limite));
        partidaRepository.saveAll(partidas);
        eventPublisher.publishEvent(new PrazoPalpiteDefinidoEvent(temporadaId, rodada + 1, limite));
    }

    private Temporada buscarTemporada(Long temporadaId) {
        return temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com ID: " + temporadaId));
    }

    // Método do círculo: o primeiro time fica fixo e os demais giram a cada rodada
    private List<Confronto> montarConfrontosDoTurno(List<Time> times) {
        List<Time> rotacao = new ArrayList<>(times);
        int n = rotacao.size();
        List<Confronto> confrontos = new ArrayList<>();

        for (int r = 0; r < n - 1; r++) {
            for (int i = 0; i < n / 2; i++) {
                Time a = rotacao.get(i);
                Time b = rotacao.get(n - 1 - i);
                // Alterna o mando do primeiro jogo da rodada para não favorecer sempre o mesmo time
                boolean inverter = i == 0 && r % 2 == 1;
                confrontos.add(inverter ? new Confronto(r + 1, b, a) : new Confronto(r + 1, a, b));
            }
            rotacao.add(1, rotacao.remove(n - 1));
        }

        return confrontos;
    }

    private Partida criarPartida(Temporada temporada, int rodada, Time mandante, Time visitante, int totalRodadas) {
        return Partida.builder()
                .temporada(temporada)
                .rodada(rodada)
                .dataHora(dataDaRodada(temporada, rodada, totalRodadas))
                .timeMandante(mandante)
                .timeVisitante(visitante)
                .local(mandante.getEstadio())
                .status(StatusPartida.AGENDADA)
                .build();
    }

    // Espalha as rodadas uniformemente entre o início e o fim da temporada
    private LocalDateTime dataDaRodada(Temporada temporada, int rodada, int totalRodadas) {
        LocalDate inicio = temporada.getDataInicio() != null ? temporada.getDataInicio() : LocalDate.now();
        LocalDate fim = temporada.getDataFim() != null ? temporada.getDataFim() : inicio.plusWeeks(totalRodadas);
        long intervaloDias = Math.max(1, ChronoUnit.DAYS.between(inicio, fim) / totalRodadas);
        return inicio.plusDays((long) (rodada - 1) * intervaloDias).atTime(HORA_PARTIDA, 0);
    }

    private void simularPartida(Partida partida, Temporada temporada,
                                Map<Long, List<Jogador>> elencosPorTime,
                                Map<EstatisticaKey, EstatisticaJogador> estatisticas,
                                List<EventoPartida> eventos) {
        List<Jogador> titularesMandante = escalar(partida.getTimeMandante(), elencosPorTime);
        List<Jogador> titularesVisitante = escalar(partida.getTimeVisitante(), elencosPorTime);

        registrarJogos(titularesMandante, temporada, estatisticas);
        registrarJogos(titularesVisitante, temporada, estatisticas);

        partida.setGolsMandante(simularGols(partida, temporada, titularesMandante, estatisticas, eventos));
        partida.setGolsVisitante(simularGols(partida, temporada, titularesVisitante, estatisticas, eventos));
        partida.setStatus(StatusPartida.FINALIZADA);

        simularCartoes(partida, temporada, titularesMandante, estatisticas, eventos);
        simularCartoes(partida, temporada, titularesVisitante, estatisticas, eventos);
    }

    // Goleiro é sempre titular; os demais são sorteados do elenco
    private List<Jogador> escalar(Time time, Map<Long, List<Jogador>> elencosPorTime) {
        List<Jogador> elenco = new ArrayList<>(elencosPorTime.getOrDefault(time.getId(), List.of()));
        Collections.shuffle(elenco, random);

        List<Jogador> titulares = new ArrayList<>();
        Set<Long> escolhidos = new HashSet<>();

        elenco.stream()
                .filter(j -> j.getPosicao() == PosicaoJogador.GOLEIRO)
                .findFirst()
                .ifPresent(goleiro -> {
                    titulares.add(goleiro);
                    escolhidos.add(goleiro.getId());
                });

        for (Jogador jogador : elenco) {
            if (titulares.size() >= TITULARES) {
                break;
            }
            if (escolhidos.add(jogador.getId())) {
                titulares.add(jogador);
            }
        }

        return titulares;
    }

    // Retorna a quantidade de gols do time e registra cada gol com autor e, às vezes, assistente
    private int simularGols(Partida partida, Temporada temporada, List<Jogador> titulares,
                            Map<EstatisticaKey, EstatisticaJogador> estatisticas,
                            List<EventoPartida> eventos) {
        int gols = sortearGols();

        for (int i = 0; i < gols; i++) {
            Jogador autor = sortearPonderado(titulares, null);
            int minuto = sortearMinuto();
            registrarEvento(partida, temporada, autor, TipoEvento.GOLO, minuto, eventos);
            atualizarEstatistica(autor, temporada, est -> est.setGols(est.getGols() + 1), estatisticas);

            if (random.nextDouble() < CHANCE_ASSISTENCIA) {
                Jogador assistente = sortearPonderado(titulares, autor);
                if (assistente != null) {
                    registrarEvento(partida, temporada, assistente, TipoEvento.ASSISTENCIA, minuto, eventos);
                    atualizarEstatistica(assistente, temporada, est -> est.setAssistencias(est.getAssistencias() + 1), estatisticas);
                }
            }
        }

        return gols;
    }

    private void simularCartoes(Partida partida, Temporada temporada, List<Jogador> titulares,
                                Map<EstatisticaKey, EstatisticaJogador> estatisticas,
                                List<EventoPartida> eventos) {
        int amarelos = random.nextInt(MAX_AMARELOS_POR_TIME + 1);
        for (int i = 0; i < amarelos; i++) {
            Jogador jogador = titulares.get(random.nextInt(titulares.size()));
            registrarEvento(partida, temporada, jogador, TipoEvento.CARTAO_AMARELO, sortearMinuto(), eventos);
            atualizarEstatistica(jogador, temporada, est -> est.setCartoesAmarelos(est.getCartoesAmarelos() + 1), estatisticas);
        }

        if (random.nextDouble() < CHANCE_VERMELHO_POR_TIME) {
            Jogador jogador = titulares.get(random.nextInt(titulares.size()));
            registrarEvento(partida, temporada, jogador, TipoEvento.CARTAO_VERMELHO, sortearMinuto(), eventos);
            atualizarEstatistica(jogador, temporada, est -> est.setCartoesVermelhos(est.getCartoesVermelhos() + 1), estatisticas);
        }
    }

    // Distribuição de Poisson (método de Knuth) com média de gols por time, limitada ao máximo
    private int sortearGols() {
        double limite = Math.exp(-MEDIA_GOLS_POR_TIME);
        int k = 0;
        double produto = 1.0;
        do {
            k++;
            produto *= random.nextDouble();
        } while (produto > limite);
        return Math.min(k - 1, MAX_GOLS_POR_TIME);
    }

    private int sortearMinuto() {
        return 1 + random.nextInt(MINUTO_FINAL);
    }

    // Sorteia um jogador com peso pela posição; o excluído (autor do gol) nunca é escolhido
    private Jogador sortearPonderado(List<Jogador> titulares, Jogador excluido) {
        Long idExcluido = excluido != null ? excluido.getId() : null;
        List<Jogador> elegiveis = titulares.stream()
                .filter(j -> !j.getId().equals(idExcluido))
                .toList();

        double total = elegiveis.stream().mapToDouble(this::pesoGol).sum();
        if (elegiveis.isEmpty() || total <= 0) {
            return null;
        }

        double sorteio = random.nextDouble() * total;
        for (Jogador jogador : elegiveis) {
            sorteio -= pesoGol(jogador);
            if (sorteio <= 0) {
                return jogador;
            }
        }
        return elegiveis.get(elegiveis.size() - 1);
    }

    // Peso de chance de marcar (ou dar assistência) por posição
    private double pesoGol(Jogador jogador) {
        if (jogador.getPosicao() == null) {
            return 1.0;
        }
        return switch (jogador.getPosicao()) {
            case ATACANTE -> 5.0;
            case MEIO_CAMPO -> 2.5;
            case DEFESA -> 1.0;
            case GOLEIRO -> 0.05;
        };
    }

    private void registrarJogos(List<Jogador> titulares, Temporada temporada,
                                Map<EstatisticaKey, EstatisticaJogador> estatisticas) {
        for (Jogador jogador : titulares) {
            atualizarEstatistica(jogador, temporada, est -> est.setJogos(est.getJogos() + 1), estatisticas);
        }
    }

    private void registrarEvento(Partida partida, Temporada temporada, Jogador jogador, TipoEvento tipo,
                                 int minuto, List<EventoPartida> eventos) {
        eventos.add(EventoPartida.builder()
                .partida(partida)
                .temporada(temporada)
                .jogador(jogador)
                .tipo(tipo)
                .minuto(minuto)
                .build());
    }

    private void atualizarEstatistica(Jogador jogador, Temporada temporada, Consumer<EstatisticaJogador> ajuste,
                                      Map<EstatisticaKey, EstatisticaJogador> estatisticas) {
        EstatisticaKey chave = new EstatisticaKey(jogador.getId(), temporada.getId());
        EstatisticaJogador estatistica = estatisticas.computeIfAbsent(chave, ignorado -> {
            EstatisticaJogador nova = new EstatisticaJogador();
            nova.setJogador(jogador);
            nova.setTemporada(temporada);
            return nova;
        });

        ajuste.accept(estatistica);
    }
}
