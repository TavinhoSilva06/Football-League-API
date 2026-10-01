package com.example.Football_League_API.service;

import com.example.Football_League_API.dto.response.ClassificacaoEntradaDto;
import com.example.Football_League_API.entity.Partida;
import com.example.Football_League_API.entity.Temporada;
import com.example.Football_League_API.enu.StatusPartida;
import com.example.Football_League_API.exception.EntityNotFoundException;
import com.example.Football_League_API.repository.PartidaRepository;
import com.example.Football_League_API.repository.ParticipacaoRepository;
import com.example.Football_League_API.repository.TemporadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço de cálculo de classificação em uma temporada.
 * Recalcula a tabela a cada requisição com base nas partidas finalizadas.
 * Não persiste estado derivado: classificação é calculada on-the-fly.
 */
@Service
@RequiredArgsConstructor
public class ClassificacaoService {

    private final TemporadaRepository temporadaRepository;
    private final ParticipacaoRepository participacaoRepository;
    private final PartidaRepository partidaRepository;

    /**
     * Calcula a classificação completa de uma temporada de pontos corridos.
     * 1. Inicializa entradas zeradas para todos os times participantes
     * 2. Processa todas as partidas finalizadas somando gols e pontos
     * 3. Ordena com o comparator apropriado e atribui posições
     *
     * @param temporadaId ID da temporada
     * @return Lista de ClassificacaoEntradaDto ordenada por posição
     * @throws EntityNotFoundException se temporada não existir
     * @throws IllegalArgumentException se formato não permite tabela de pontos (ex: MATA_MATA)
     */
    public List<ClassificacaoEntradaDto> calcularClassificacao(Long temporadaId) {
        Temporada temporada = temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new EntityNotFoundException("Temporada não encontrada com ID: " + temporadaId));

        // Inicializar entradas zeradas para todos os times da temporada
        Map<Long, ClassificacaoEntradaDto> classificacao = inicializarClassificacao(temporada);

        // Processar todas as partidas finalizadas
        List<Partida> partidas = partidaRepository.findByTemporadaIdAndStatus(temporadaId, StatusPartida.FINALIZADA);
        for (Partida partida : partidas) {
            processarPartida(partida, classificacao);
        }

        // Ordenar e atribuir posições
        Comparator<ClassificacaoEntradaDto> comparator = TiebreakStrategyResolver.resolve(temporada.getCampeonato());
        List<ClassificacaoEntradaDto> resultado = classificacao.values()
                .stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        for (int i = 0; i < resultado.size(); i++) {
            resultado.get(i).setPosicao(i + 1);
        }

        return resultado;
    }

    /**
     * Inicializa mapa de classificação com um time por cada participação.
     * Todos começam com 0 jogos, 0 gols, 0 pontos.
     */
    private Map<Long, ClassificacaoEntradaDto> inicializarClassificacao(Temporada temporada) {
        Map<Long, ClassificacaoEntradaDto> mapa = new HashMap<>();

        participacaoRepository.findByTemporadaId(temporada.getId())
                .forEach(participacao -> {
                    mapa.put(participacao.getTime().getId(),
                            ClassificacaoEntradaDto.builder()
                                    .timeId(participacao.getTime().getId())
                                    .timeNome(participacao.getTime().getNome())
                                    .jogos(0)
                                    .vitorias(0)
                                    .empates(0)
                                    .derrotas(0)
                                    .golsPro(0)
                                    .golsContra(0)
                                    .saldoGols(0)
                                    .pontos(0)
                                    .build()
                    );
                });

        return mapa;
    }

    /**
     * Processa uma partida finalizada somando dados aos dois times envolvidos.
     * Atualiza jogos, gols, vitórias/empates/derrotas e pontos.
     */
    private void processarPartida(Partida partida, Map<Long, ClassificacaoEntradaDto> classificacao) {
        Long timeMandanteId = partida.getTimeMandante().getId();
        Long timeVisitanteId = partida.getTimeVisitante().getId();

        Integer golsMandante = partida.getGolsMandante() != null ? partida.getGolsMandante() : 0;
        Integer golsVisitante = partida.getGolsVisitante() != null ? partida.getGolsVisitante() : 0;

        // Atualizar time mandante
        ClassificacaoEntradaDto mandante = classificacao.get(timeMandanteId);
        if (mandante != null) {
            mandante.setJogos(mandante.getJogos() + 1);
            mandante.setGolsPro(mandante.getGolsPro() + golsMandante);
            mandante.setGolsContra(mandante.getGolsContra() + golsVisitante);

            if (golsMandante > golsVisitante) {
                mandante.setVitorias(mandante.getVitorias() + 1);
                mandante.setPontos(mandante.getPontos() + 3);
            } else if (golsMandante < golsVisitante) {
                mandante.setDerrotas(mandante.getDerrotas() + 1);
            } else {
                mandante.setEmpates(mandante.getEmpates() + 1);
                mandante.setPontos(mandante.getPontos() + 1);
            }

            mandante.setSaldoGols(mandante.getGolsPro() - mandante.getGolsContra());
        }

        // Atualizar time visitante
        ClassificacaoEntradaDto visitante = classificacao.get(timeVisitanteId);
        if (visitante != null) {
            visitante.setJogos(visitante.getJogos() + 1);
            visitante.setGolsPro(visitante.getGolsPro() + golsVisitante);
            visitante.setGolsContra(visitante.getGolsContra() + golsMandante);

            if (golsVisitante > golsMandante) {
                visitante.setVitorias(visitante.getVitorias() + 1);
                visitante.setPontos(visitante.getPontos() + 3);
            } else if (golsVisitante < golsMandante) {
                visitante.setDerrotas(visitante.getDerrotas() + 1);
            } else {
                visitante.setEmpates(visitante.getEmpates() + 1);
                visitante.setPontos(visitante.getPontos() + 1);
            }

            visitante.setSaldoGols(visitante.getGolsPro() - visitante.getGolsContra());
        }
    }
}
