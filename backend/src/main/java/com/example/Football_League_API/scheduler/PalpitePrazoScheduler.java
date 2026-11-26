package com.example.Football_League_API.scheduler;

import com.example.Football_League_API.enu.StatusPartida;
import com.example.Football_League_API.repository.PartidaRepository;
import com.example.Football_League_API.service.SimulacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PalpitePrazoScheduler {

    private final PartidaRepository partidaRepository;
    private final SimulacaoService simulacaoService;

    @Value("${palpite.simulacao.habilitada:true}")
    private boolean habilitada;

    @Scheduled(cron = "${palpite.simulacao.cron:*/5 * * * * *}")
    public void simularRodadasExpiradas() {
        if (!habilitada) {
            return;
        }

        LocalDateTime agora = LocalDateTime.now();
        List<Long> temporadaIds = partidaRepository.findTemporadaIdsComPrazoExpirado(
                StatusPartida.AGENDADA, agora);

        for (Long temporadaId : temporadaIds) {
            try {
                partidaRepository.findFirstByTemporadaIdAndStatusAndDataLimitePalpiteNotNullOrderByRodadaAsc(
                                temporadaId, StatusPartida.AGENDADA)
                        .filter(partida -> !agora.isBefore(partida.getDataLimitePalpite()))
                        .ifPresent(partida -> simulacaoService.simularRodada(temporadaId, partida.getRodada()));
            } catch (IllegalArgumentException e) {
                log.debug("Simulação automática ignorada para temporada {}: {}", temporadaId, e.getMessage());
            } catch (Exception e) {
                log.error("Falha ao simular rodada expirada da temporada {}", temporadaId, e);
            }
        }
    }
}
