package com.example.Football_League_API.external.config;

import com.example.Football_League_API.external.service.FootballApiSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FootballApiScheduledSync {

    private final FootballApiSyncService syncService;

    @Value("${football.api.sync.scheduled:true}")
    private boolean scheduledSyncEnabled;

    @Scheduled(cron = "0 0 2 * * *")
    public void syncDailyAtTwoAm() {
        if (!scheduledSyncEnabled) {
            return;
        }

        log.info("\n╔════════════════════════════════════════════════════════════════╗");
        log.info("║ Sincronização agendada iniciada (2:00 AM)                      ║");
        log.info("╚════════════════════════════════════════════════════════════════╝\n");

        try {
            FootballApiSyncService.SyncResult result = syncService.syncAll(false);

            if (result.isSkipped()) {
                log.info("Sincronização agendada: dados já estão atualizados");
            } else {
                log.info("✓ Sincronização agendada concluída: {} times sincronizados",
                    result.getTimesSincronizados());
            }

        } catch (Exception e) {
            log.error("❌ Erro na sincronização agendada: {}", e.getMessage(), e);
        }
    }
}
