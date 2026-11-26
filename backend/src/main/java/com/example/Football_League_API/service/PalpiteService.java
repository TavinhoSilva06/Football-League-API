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
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.mapper.PalpiteMapper;
import com.example.Football_League_API.repository.PalpiteRepository;
import com.example.Football_League_API.repository.PartidaRepository;
import com.example.Football_League_API.repository.TemporadaRepository;
import com.example.Football_League_API.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
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
                    .build();
        }

        Integer rodada = atual.get().getRodada();
        Usuario usuario = usuarioAutenticado();
        Map<Long, Palpite> palpites = palpiteRepository
                .findByUsuarioIdAndPartida_Temporada_IdAndPartida_Rodada(usuario.getId(), temporadaId, rodada)
                .stream()
                .collect(Collectors.toMap(p -> p.getPartida().getId(), p -> p));

        List<JogoPalpiteResponseDto> jogos = partidaRepository.findByTemporadaIdAndRodada(temporadaId, rodada)
                .stream()
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
                .jogos(jogos)
                .palpitesEnviados(enviados)
                .acertos(acertos)
                .pontosGanhos(pontos)
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

        return porRodada.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .map(rodada -> {
                    Map<Long, Palpite> palpites = porRodada.get(rodada).stream()
                            .collect(Collectors.toMap(p -> p.getPartida().getId(), p -> p));

                    List<JogoPalpiteResponseDto> jogos = partidaRepository.findByTemporadaIdAndRodada(temporadaId, rodada)
                            .stream()
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
                            .build();
                })
                .toList();
    }

    @Transactional
    public PalpiteResponseDto salvar(PalpiteRequestDto dto) {
        Usuario usuario = usuarioAutenticado();
        Partida partida = partidaRepository.findById(dto.getPartidaId())
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com id: " + dto.getPartidaId()));

        if (StatusPartida.AGENDADA != partida.getStatus()) {
            throw new IllegalArgumentException("Palpites encerrados para esta partida");
        }

        Palpite palpite = palpiteRepository.findByUsuarioIdAndPartidaId(usuario.getId(), partida.getId())
                .orElseGet(() -> Palpite.builder()
                        .usuario(usuario)
                        .partida(partida)
                        .criadoEm(LocalDateTime.now())
                        .build());
        palpite.setGolsMandante(dto.getGolsMandante());
        palpite.setGolsVisitante(dto.getGolsVisitante());

        return mapper.toResponseDto(palpiteRepository.save(palpite));
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

        for (Map.Entry<Long, Integer> entrada : ganhosPorUsuario.entrySet()) {
            Usuario usuario = usuarioRepository.findById(entrada.getKey())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + entrada.getKey()));
            int pontosAtuais = usuario.getPontos() == null ? 0 : usuario.getPontos();
            usuario.setPontos(pontosAtuais + entrada.getValue());
            usuarioRepository.save(usuario);
        }

        return new AvaliacaoRodada(avaliados, distribuidos);
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
                .status(partida.getStatus())
                .golsMandante(partida.getGolsMandante())
                .golsVisitante(partida.getGolsVisitante())
                .palpiteId(palpite == null ? null : palpite.getId())
                .palpiteMandante(palpite == null ? null : palpite.getGolsMandante())
                .palpiteVisitante(palpite == null ? null : palpite.getGolsVisitante())
                .acerto(palpite == null ? null : palpite.getAcerto())
                .pontosGanhos(palpite == null ? null : palpite.getPontosGanhos())
                .bloqueado(StatusPartida.AGENDADA != partida.getStatus())
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
