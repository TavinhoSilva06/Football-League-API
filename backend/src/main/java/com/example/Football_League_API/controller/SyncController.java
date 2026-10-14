package com.example.Football_League_API.controller;

import com.example.Football_League_API.external.service.FootballApiSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SyncController {

    private final FootballApiSyncService syncService;

    @PostMapping("/football-api")
    public ResponseEntity<FootballApiSyncService.SyncResult> syncFootballApi(
            @RequestParam(required = false, defaultValue = "false") boolean force) {

        log.info("Requisicao de sincronizacao recebida (force={})", force);

        FootballApiSyncService.SyncResult result = syncService.syncAll(force);

        if (result.isApiAvailable()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(503).body(result);
        }
    }
}
