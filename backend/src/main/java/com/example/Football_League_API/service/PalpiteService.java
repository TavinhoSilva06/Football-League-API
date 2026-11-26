package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.request.PalpiteRequestDto;
import com.example.Football_League_API.dto.response.JogoPalpiteResponseDto;
import com.example.Football_League_API.dto.response.PalpiteResponseDto;
import com.example.Football_League_API.dto.response.RodadaPalpitesResponseDto;
import com.example.Football_League_API.entity.Palpite;
import com.example.Football_League_API.entity.Partida;
import com.example.Football_League_API.entity.Temporada;
import com.example.Football_League_API.entity.Usuario;
import com.example.Football_League_API.enu.Patente;
import com.example.Football_League_API.enu.StatusPartida;
import com.example.Football_League_API.event.PrazoPalpiteDefinidoEvent;
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.mapper.PalpiteMapper;
import com.example.Football_League_API.repository.PalpiteRepository;
import com.example.Football_League_API.repository.PartidaRepository;
import com.example.Football_League_API.repository.TemporadaRepository;
import com.example.Football_League_API.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PalpiteService {

    private final PalpiteRepository palpiteRepository;
    private final PartidaRepository partidaRepository;
    private final TemporadaRepository temporadaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PalpiteMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${palpite.prazo-minutos:4}")
    private int prazoMinutos;

    @Transactional
    public RodadaPalpitesResponseDto listarRodadaAtual(Long temporadaId) {
        Temporada temporada = temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com id: " + temporadaId));

        Optional<Partida> atual = partidaRepository.findFirstByTemporadaIdAndStatusOrderByRodadaAsc(
                temporadaId, StatusPartida.AGENDADA);
        if (atual.isEmpty()) {
            return RodadaPalpitesResponseDto.builder()
                    .temporadaId(temporada.getId())
                    .rodada(null)
                    .jogos(List.of())
                    .palpitesEnviados(0)
                    .acertos(0)
                    .pontosGanhos(0)
                    .semPalpites(false)
                    .build();
        }

        Integer rodada = atual.get().getRodada();
        List<Partida> partidasDaRodada = partidaRepository.findByTemporadaIdAndRodada(temporadaId, rodada);
        garantirPrazo(partidasDaRodada);
        LocalDateTime dataLimite = partidasDaRodada.get(0).getDataLimitePalpite();

        Usuario usuario = usuarioAutenticado();
        Map<Long, Palpite> palpites = palpiteRepository
                .findByUsuarioIdAndPartida_Temporada_IdAndPartida_Rodada(usuario.getId(), temporadaId, rodada)
                .stream()
                .collect(Collectors.toMap(p -> p.getPartida().getId(), p -> p));

        List<JogoPalpiteResponseDto> jogos = partidasDaRodada.stream()
                .map(partida -> paraJogo(partida, palpites.get(partida.getId())))
                .toList();

        int enviados = 0;
        int acertos = 0;
        int pontos = 0;
        for (Palpite palpite : palpites.values()) {
            enviados++;
            if (Boolean.TRUE.equals(palpite.getAcerto())) {
                acertos++;
            }
            pontos += palpite.getPontosGanhos() == null ? 0 : palpite.getPontosGanhos();
        }

        return RodadaPalpitesResponseDto.builder()
                .temporadaId(temporadaId)
                .rodada(rodada)
                .dataLimitePalpite(dataLimite)
                .jogos(jogos)
                .palpitesEnviados(enviados)
                .acertos(acertos)
                .pontosGanhos(pontos)
                .semPalpites(false)
                .build();
    }

    public List<RodadaPalpitesResponseDto> listarResultados(Long temporadaId) {
        temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com ID: " + temporadaId));

        Usuario usuario = usuarioAutenticado();
        Map<Integer, List<Palpite>> porRodada = palpiteRepository
                .findByUsuarioIdAndPartida_Temporada_Id(usuario.getId(), temporadaId)
                .stream()
                .filter(palpite -> palpite.getAcerto() != null)
                .collect(Collectors.groupingBy(palpite -> palpite.getPartida().getRodada()));

        Map<Integer, List<Partida>> partidasPorRodada = partidaRepository
                .findByTemporadaIdAndStatus(temporadaId, StatusPartida.FINALIZADA)
                .stream()
                .collect(Collectors.groupingBy(Partida::getRodada));

        return partidasPorRodada.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .map(rodada -> {
                    Map<Long, Palpite> palpites = porRodada.getOrDefault(rodada, List.of()).stream()
                            .collect(Collectors.toMap(p -> p.getPartida().getId(), p -> p));

                    List<JogoPalpiteResponseDto> jogos = partidasPorRodada.get(rodada).stream()
                            .filter(partida -> palpites.containsKey(partida.getId()))
                            .map(partida -> paraJogo(partida, palpites.get(partida.getId())))
                            .toList();

                    int acertos = 0;
                    int pontos = 0;
                    for (JogoPalpiteResponseDto jogo : jogos) {
                        if (Boolean.TRUE.equals(jogo.getAcerto())) acertos++;
                        pontos += jogo.getPontosGanhos() == null ? 0 : jogo.getPontosGanhos();
                    }

                    return RodadaPalpitesResponseDto.builder()
                            .temporadaId(temporadaId)
                            .rodada(rodada)
                            .jogos(jogos)
                            .palpitesEnviados(jogos.size())
                            .acertos(acertos)
                            .pontosGanhos(pontos)
                            .semPalpites(palpites.isEmpty())
                            .build();
                })
                .toList();
    }

    @Transactional
    public PalpiteResponseDto salvar(PalpiteRequestDto dto) {
        Usuario usuario = usuarioAutenticado();
        Partida partida = buscarPartida(dto.getPartidaId());
        garantirPrazo(List.of(partida));
        validarPrazo(partida);

        return mapper.toResponseDto(palpiteRepository.save(montarPalpite(usuario, partida, dto)));
    }

    @Transactional
    public List<PalpiteResponseDto> salvarLote(List<PalpiteRequestDto> dtos) {
        Usuario usuario = usuarioAutenticado();
        Map<Long, PalpiteRequestDto> ultimosPorPartida = new LinkedHashMap<>();
        for (PalpiteRequestDto dto : dtos) {
            ultimosPorPartida.put(dto.getPartidaId(), dto);
        }

        List<Partida> partidas = new ArrayList<>();
        Map<Long, Partida> partidasPorId = new HashMap<>();
        for (PalpiteRequestDto dto : ultimosPorPartida.values()) {
            Partida partida = buscarPartida(dto.getPartidaId());
            partidas.add(partida);
            partidasPorId.put(partida.getId(), partida);
        }

        garantirPrazo(partidas);
        List<Palpite> palpites = new ArrayList<>();
        for (PalpiteRequestDto dto : ultimosPorPartida.values()) {
            Partida partida = partidasPorId.get(dto.getPartidaId());
            validarPrazo(partida);
            palpites.add(montarPalpite(usuario, partida, dto));
        }

        return palpiteRepository.saveAll(palpites).stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Transactional
    public AvaliacaoRodada avaliarRodada(Long temporadaId, Integer rodada) {
        List<Palpite> palpites = palpiteRepository.findByPartida_Temporada_IdAndPartida_Rodada(temporadaId, rodada);
        Map<Long, Integer> ganhosPorUsuario = new HashMap<>();
        int avaliados = 0;
        int distribuidos = 0;

        for (Palpite palpite : palpites) {
            Partida partida = palpite.getPartida();
            if (palpite.getPontosGanhos() != null || partida.getStatus() != StatusPartida.FINALIZADA
                    || partida.getGolsMandante() == null || partida.getGolsVisitante() == null) {
                continue;
            }

            Usuario usuario = palpite.getUsuario();
            int pontosAtuais = usuario.getPontos() == null ? 0 : usuario.getPontos();
            Patente patente = Patente.fromPontos(pontosAtuais);
            boolean acerto = Objects.equals(palpite.getGolsMandante(), partida.getGolsMandante())
                    && Objects.equals(palpite.getGolsVisitante(), partida.getGolsVisitante());
            int ganho = acerto ? patente.getPontosPorAcerto() : 0;

            palpite.setAcerto(acerto);
            palpite.setPontosGanhos(ganho);
            ganhosPorUsuario.merge(usuario.getId(), ganho, Integer::sum);
            avaliados++;
            distribuidos += ganho;
        }

        if (!palpites.isEmpty()) {
            palpiteRepository.saveAll(palpites);
        }

        Map<Long, Usuario> usuarios = usuarioRepository.findAllById(ganhosPorUsuario.keySet()).stream()
                .collect(Collectors.toMap(Usuario::getId, usuario -> usuario));

        for (Map.Entry<Long, Integer> entrada : ganhosPorUsuario.entrySet()) {
            Usuario usuario = usuarios.get(entrada.getKey());
            if (usuario == null) {
                throw new EntityNotFoundException("Usuário não encontrado com id: " + entrada.getKey());
            }
            int pontosAtuais = usuario.getPontos() == null ? 0 : usuario.getPontos();
            usuario.setPontos(pontosAtuais + entrada.getValue());
        }
        usuarioRepository.saveAll(usuarios.values());

        return new AvaliacaoRodada(avaliados, distribuidos);
    }

    private Partida buscarPartida(Long partidaId) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com id: " + partidaId));
        if (StatusPartida.AGENDADA != partida.getStatus()) {
            throw new IllegalArgumentException("Palpites encerrados para esta partida");
        }
        return partida;
    }

    private void validarPrazo(Partida partida) {
        if (prazoExpirado(partida)) {
            throw new IllegalArgumentException("Prazo para palpites desta rodada encerrado");
        }
    }

    private Palpite montarPalpite(Usuario usuario, Partida partida, PalpiteRequestDto dto) {
        Palpite palpite = palpiteRepository.findByUsuarioIdAndPartidaId(usuario.getId(), partida.getId())
                .orElseGet(() -> Palpite.builder()
                        .usuario(usuario)
                        .partida(partida)
                        .criadoEm(LocalDateTime.now())
                        .build());
        palpite.setGolsMandante(dto.getGolsMandante());
        palpite.setGolsVisitante(dto.getGolsVisitante());
        return palpite;
    }

    private void garantirPrazo(List<Partida> partidas) {
        if (partidas.isEmpty()) {
            return;
        }

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limite = agora.plusMinutes(prazoMinutos);
        boolean alterado = false;
        for (Partida partida : partidas) {
            if (partida.getDataLimitePalpite() == null) {
                partida.setDataLimitePalpite(limite);
                alterado = true;
            }
        }

        if (!alterado) {
            return;
        }

        partidaRepository.saveAll(partidas);
        Long temporadaId = partidas.get(0).getTemporada().getId();
        partidas.stream()
                .map(Partida::getRodada)
                .distinct()
                .forEach(rodada -> eventPublisher.publishEvent(
                        new PrazoPalpiteDefinidoEvent(temporadaId, rodada, limite)));
    }

    private boolean prazoExpirado(Partida partida) {
        return partida.getDataLimitePalpite() == null
                || !LocalDateTime.now().isBefore(partida.getDataLimitePalpite());
    }

    private JogoPalpiteResponseDto paraJogo(Partida partida, Palpite palpite) {
        return JogoPalpiteResponseDto.builder()
                .partidaId(partida.getId())
                .rodada(partida.getRodada())
                .mandanteId(partida.getTimeMandante() == null ? null : partida.getTimeMandante().getId())
                .mandanteNome(partida.getTimeMandante() == null ? null : partida.getTimeMandante().getNome())
                .mandanteEscudoUrl(partida.getTimeMandante() == null ? null : partida.getTimeMandante().getEscudoUrl())
                .visitanteId(partida.getTimeVisitante() == null ? null : partida.getTimeVisitante().getId())
                .visitanteNome(partida.getTimeVisitante() == null ? null : partida.getTimeVisitante().getNome())
                .visitanteEscudoUrl(partida.getTimeVisitante() == null ? null : partida.getTimeVisitante().getEscudoUrl())
                .dataHora(partida.getDataHora())
                .dataLimitePalpite(partida.getDataLimitePalpite())
                .status(partida.getStatus())
                .golsMandante(partida.getGolsMandante())
                .golsVisitante(partida.getGolsVisitante())
                .palpiteId(palpite == null ? null : palpite.getId())
                .palpiteMandante(palpite == null ? null : palpite.getGolsMandante())
                .palpiteVisitante(palpite == null ? null : palpite.getGolsVisitante())
                .acerto(palpite == null ? null : palpite.getAcerto())
                .pontosGanhos(palpite == null ? null : palpite.getPontosGanhos())
                .bloqueado(StatusPartida.AGENDADA != partida.getStatus() || prazoExpirado(partida))
                .build();
    }

    private Usuario usuarioAutenticado() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null || autenticacao.getName() == null) {
            throw new IllegalArgumentException("Usuário não autenticado");
        }
        return usuarioRepository.findByEmail(autenticacao.getName())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + autenticacao.getName()));
    }

    public record AvaliacaoRodada(int palpitesAvaliados, int pontosDistribuidos) {}
}
