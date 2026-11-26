package com.example.Football_League_API.scheduler;

import com.example.Football_League_API.event.PrazoPalpiteDefinidoEvent;
import com.example.Football_League_API.service.SimulacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PalpitePrazoAgendador {

    private final TaskScheduler taskScheduler;
    private final SimulacaoService simulacaoService;

    private final ConcurrentMap<String, Agendamento> agendamentos = new ConcurrentHashMap<>();

    private record Agendamento(ScheduledFuture<?> future, Instant limite) {
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void agendar(PrazoPalpiteDefinidoEvent event) {
        if (event.temporadaId() == null || event.rodada() == null || event.dataLimite() == null) {
            return;
        }

        Instant limite = event.dataLimite().atZone(ZoneId.systemDefault()).toInstant();
        if (!limite.isAfter(Instant.now())) {
            return;
        }

        String chave = event.temporadaId() + ":" + event.rodada();
        agendamentos.compute(chave, (ignorado, atual) -> {
            if (atual != null && atual.limite().equals(limite) && !atual.future().isDone()) {
                return atual;
            }
            if (atual != null) {
                atual.future().cancel(false);
            }
            ScheduledFuture<?> future = taskScheduler.schedule(
                    () -> executar(event.temporadaId(), event.rodada(), chave), limite);
            return new Agendamento(future, limite);
        });
    }

    private void executar(Long temporadaId, Integer rodada, String chave) {
        try {
            log.info("Simulação automática da rodada {} da temporada {}", rodada, temporadaId);
            simulacaoService.simularRodada(temporadaId, rodada);
        } catch (IllegalArgumentException e) {
            log.debug("Simulação automática ignorada: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Falha na simulação automática da rodada {} da temporada {}", rodada, temporadaId, e);
        } finally {
            agendamentos.remove(chave);
        }
    }
}
