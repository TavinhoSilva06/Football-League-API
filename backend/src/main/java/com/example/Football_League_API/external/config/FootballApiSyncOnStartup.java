package com.example.Football_League_API.external.config;

import com.example.Football_League_API.external.service.FootballApiSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FootballApiSyncOnStartup {

    private final FootballApiSyncService syncService;

    @Value("${football.api.sync.on-startup:true}")
    private boolean syncOnStartup;

    @EventListener(ApplicationReadyEvent.class)
    public void syncOnApplicationReady() {
        if (!syncOnStartup) {
            log.info("Sincronização automática no startup desabilitada");
            return;
        }

        log.info("\n╔════════════════════════════════════════════════════════════════╗");
        log.info("║ Aplicação iniciada. Sincronizando com API-Football...          ║");
        log.info("╚════════════════════════════════════════════════════════════════╝\n");

        try {
            FootballApiSyncService.SyncResult result = syncService.syncAll();

            if (result.isSkipped()) {
                log.info("Sincronização pulada: dados já estão atualizados");
            } else if (result.isApiAvailable()) {
                log.info("✓ Sincronização no startup concluída com sucesso");
            } else {
                log.warn("⚠ Sincronização no startup falhou, mas aplicação continuará funcionando");
            }

        } catch (Exception e) {
            log.error("❌ Erro na sincronização no startup: {}", e.getMessage(), e);
            log.warn("⚠ A aplicação continuará funcionando. Use dados do banco existente.");
        }
    }
}
