package com.example.Football_League_API.external.service;

import com.example.Football_League_API.entity.Campeonato;
import com.example.Football_League_API.entity.SincronizacaoLog;
import com.example.Football_League_API.entity.Temporada;
import com.example.Football_League_API.entity.Time;
import com.example.Football_League_API.enu.FormatoCampeonato;
import com.example.Football_League_API.enu.StatusTemporada;
import com.example.Football_League_API.enu.TipoCampeonato;
import com.example.Football_League_API.external.client.FootballApiClient;
import com.example.Football_League_API.external.dto.FootballApiCompetitionDto;
import com.example.Football_League_API.external.dto.FootballApiSeasonDto;
import com.example.Football_League_API.external.dto.FootballApiTeamDto;
import com.example.Football_League_API.external.exception.FootballApiException;
import com.example.Football_League_API.external.exception.SyncException;
import com.example.Football_League_API.repository.CampeonatoRepository;
import com.example.Football_League_API.repository.SincronizacaoLogRepository;
import com.example.Football_League_API.repository.TemporadaRepository;
import com.example.Football_League_API.repository.TimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FootballApiSyncService {

    private final FootballApiClient footballApiClient;
    private final CampeonatoRepository campeonatoRepository;
    private final TemporadaRepository temporadaRepository;
    private final TimeRepository timeRepository;
    private final SincronizacaoLogRepository syncLogRepository;

    @Value("${football.api.sync.interval-hours:24}")
    private int syncIntervalHours;

    // Ligas (ids da API-Football) cujos times são sincronizados, para não estourar a cota diária
    @Value("${football.api.sync.league-ids:}")
    private List<Integer> leagueIds;

    // Temporada cujos times são sincronizados (o plano Free só libera 2022 a 2024)
    @Value("${football.api.sync.team-season:2024}")
    private int teamSeason;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Transactional
    public SyncResult syncAll() {
        return syncAll(false);
    }

    @Transactional
    public SyncResult syncAll(boolean forceSync) {
        log.info("INICIANDO SINCRONIZACAO COM API-FOOTBALL");

        if (!forceSync && !shouldSync()) {
            log.info("Ultima sincronizacao foi ha menos de {}h. Pulando...", syncIntervalHours);
            return createSkippedResult();
        }

        SyncResult result = new SyncResult();
        long startTime = System.currentTimeMillis();

        try {
            List<TeamSyncTarget> teamTargets = new ArrayList<>();
            syncCompetitions(result, teamTargets);
            syncTeamsForAllCompetitions(teamTargets, result);

            long duration = System.currentTimeMillis() - startTime;
            result.setDurationMs(duration);

            logSyncResult(result);
            logSyncToDatabase(result);

        } catch (FootballApiException e) {
            log.error("Erro na API-Football: {}", e.getMessage());
            result.addError("API-Football", e.getMessage());
            result.setApiAvailable(false);
            logSyncToDatabase(result);
        } catch (Exception e) {
            log.error("Erro inesperado durante sincronizacao: {}", e.getMessage(), e);
            result.addError("Sincronizacao", e.getMessage());
            logSyncToDatabase(result);
        }

        return result;
    }

    private boolean shouldSync() {
        LocalDateTime dataLimite = LocalDateTime.now().minusHours(syncIntervalHours);
        List<SincronizacaoLog> ultimasSincronizacoes = syncLogRepository.findLastSuccessfulSync("GERAL", dataLimite);
        return ultimasSincronizacoes.isEmpty();
    }

    private void syncCompetitions(SyncResult result, List<TeamSyncTarget> teamTargets) {
        log.info("Sincronizando CAMPEONATOS...");

        try {
            List<FootballApiCompetitionDto> competitions = footballApiClient.getCompetitions();

            if (competitions == null || competitions.isEmpty()) {
                log.warn("Nenhuma competicao retornada pela API");
                result.addWarning("Campeonatos", "Resposta vazia da API");
                return;
            }

            for (FootballApiCompetitionDto compDto : competitions) {
                try {
                    // Só grava as ligas configuradas, para não sobrecarregar o banco e a cota da API
                    if (leagueIds == null || !leagueIds.contains(compDto.getId())) {
                        continue;
                    }

                    if (!compDto.isValid()) {
                        log.warn("Pulando competicao invalida: {}", compDto);
                        result.addWarning("Campeonato", "Dados incompletos");
                        continue;
                    }

                    syncSingleCompetition(compDto, result, teamTargets);
                    result.incrementCompeticoes();

                } catch (Exception e) {
                    log.error("Erro na competicao: {}", e.getMessage());
                    result.addError("Campeonato", e.getMessage());
                }
            }

        } catch (FootballApiException e) {
            log.error("Falha ao buscar competicoes: {}", e.getMessage());
            result.addError("Campeonatos", e.getMessage());
            throw e;
        }
    }

    private void syncSingleCompetition(FootballApiCompetitionDto compDto, SyncResult result, List<TeamSyncTarget> teamTargets) {
        try {
            Optional<Campeonato> existing = campeonatoRepository.findByApiId(compDto.getId());

            Campeonato campeonato = existing.orElseGet(() -> Campeonato.builder()
                    .apiId(compDto.getId())
                    .nome(nomeCampeonato(compDto))
                    .pais(compDto.getOrDefaultCountry())
                    .tipo(TipoCampeonato.LIGA_NACIONAL)
                    .formato(FormatoCampeonato.PONTOS_CORRIDOS)
                    .descricao("Sincronizado de API-Football")
                    .build());

            if (existing.isEmpty()) {
                campeonato = campeonatoRepository.save(campeonato);
                log.info("Campeonato criado: {}", campeonato.getNome());
            }

            syncSeasonsForCompetition(compDto, campeonato, result, teamTargets);

        } catch (DataIntegrityViolationException e) {
            log.warn("Erro ao sincronizar: {}", e.getMessage());
        }
    }

    // Nomes exibidos para ligas específicas (id da API-Football -> nome no sistema)
    private static final Map<Integer, String> NOMES_PERSONALIZADOS = Map.of(
            71, "Brasileirão Série A");

    // Nomes de ligas se repetem entre países (ex.: "Serie A" da Itália e do Brasil), então inclui o país
    private String nomeCampeonato(FootballApiCompetitionDto compDto) {
        String personalizado = NOMES_PERSONALIZADOS.get(compDto.getId());
        if (personalizado != null) {
            return personalizado;
        }
        return compDto.getOrDefaultName() + " (" + compDto.getOrDefaultCountry() + ")";
    }

    // As temporadas já vêm dentro da resposta de /leagues, então não precisam de chamada extra
    private void syncSeasonsForCompetition(FootballApiCompetitionDto compDto, Campeonato campeonato,
                                           SyncResult result, List<TeamSyncTarget> teamTargets) {
        for (FootballApiSeasonDto seasonDto : compDto.getSeasons()) {
            if (!seasonDto.isValid()) {
                continue;
            }

            syncSingleSeason(seasonDto, campeonato, result);
            result.incrementTemporadas();

            boolean ligaConfigurada = leagueIds != null && leagueIds.contains(compDto.getId());
            if (ligaConfigurada && seasonDto.getYear() == teamSeason) {
                teamTargets.add(new TeamSyncTarget(compDto.getId(), seasonDto.getYear()));
            }
        }
    }

    private void syncSingleSeason(FootballApiSeasonDto seasonDto, Campeonato campeonato, SyncResult result) {
        try {
            String seasonName = seasonDto.getOrDefaultYear();
            Optional<Temporada> existing = temporadaRepository.findByCampeonatoIdAndNome(campeonato.getId(), seasonName);

            if (existing.isEmpty()) {
                Temporada temporada = Temporada.builder()
                        .campeonato(campeonato)
                        .nome(seasonName)
                        .dataInicio(parseDate(seasonDto.getStart()))
                        .dataFim(parseDate(seasonDto.getEnd()))
                        .status(StatusTemporada.PLANEJADA)
                        .build();

                temporadaRepository.save(temporada);
                log.info("Temporada criada: {}", seasonName);
            }

        } catch (DataIntegrityViolationException e) {
            log.warn("Erro ao sincronizar: {}", e.getMessage());
        }
    }

    private void syncTeamsForAllCompetitions(List<TeamSyncTarget> teamTargets, SyncResult result) {
        log.info("Sincronizando TIMES de {} liga(s) configurada(s)...", teamTargets.size());

        for (TeamSyncTarget target : teamTargets) {
            syncTeamsForCompetitionAndSeason(target.leagueId(), target.season(), result);
        }
    }

    private void syncTeamsForCompetitionAndSeason(Integer leagueId, Integer season, SyncResult result) {
        try {
            List<FootballApiTeamDto> teams = footballApiClient.getTeams(leagueId, season);

            if (teams == null || teams.isEmpty()) {
                return;
            }

            for (FootballApiTeamDto teamDto : teams) {
                if (teamDto.isValid()) {
                    syncSingleTeam(teamDto, result);
                    result.incrementTimes();
                }
            }

        } catch (FootballApiException e) {
            log.debug("Erro ao buscar times: {}", e.getMessage());
        }
    }

    private void syncSingleTeam(FootballApiTeamDto teamDto, SyncResult result) {
        try {
            Optional<Time> existing = timeRepository.findByNome(teamDto.getOrDefaultName());

            if (existing.isEmpty()) {
                Time time = Time.builder()
                        .nome(teamDto.getOrDefaultName())
                        .sigla(teamDto.getOrDefaultCode())
                        .pais(teamDto.getOrDefaultCountry())
                        .escudoUrl(teamDto.getLogo())
                        .build();

                timeRepository.save(time);
                log.info("Time criado: {}", time.getNome());
            }

        } catch (DataIntegrityViolationException e) {
            log.debug("Erro ao sincronizar: {}", e.getMessage());
        }
    }

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private void logSyncResult(SyncResult result) {
        log.info("Campeonatos: {}", result.getCompeticoesSincronizadas());
        log.info("Temporadas: {}", result.getTemporadasSincronizadas());
        log.info("Times: {}", result.getTimesSincronizados());
        log.info("Tempo: {}ms", result.getDurationMs());
    }

    private void logSyncToDatabase(SyncResult result) {
        try {
            int total = result.getCompeticoesSincronizadas() +
                    result.getTemporadasSincronizadas() +
                    result.getTimesSincronizados();

            String status = result.getErrors().isEmpty() ? "SUCESSO" : "ERRO";

            SincronizacaoLog syncLog = SincronizacaoLog.builder()
                    .tipo("GERAL")
                    .dataSync(LocalDateTime.now())
                    .quantidadeSincronizada(total)
                    .status(status)
                    .mensagemErro(null)
                    .build();

            syncLogRepository.save(syncLog);
            log.info("Sincronizacao registrada");

        } catch (Exception e) {
            log.error("Erro ao registrar: {}", e.getMessage());
        }
    }

    private SyncResult createSkippedResult() {
        SyncResult result = new SyncResult();
        result.setSkipped(true);
        return result;
    }

    // Liga e temporada atual cujos times devem ser sincronizados (ids da API-Football)
    private record TeamSyncTarget(Integer leagueId, Integer season) {}

    public static class SyncResult {
        private int competicoesSincronizadas = 0;
        private int temporadasSincronizadas = 0;
        private int timesSincronizados = 0;
        private long durationMs = 0;
        private boolean apiAvailable = true;
        private boolean skipped = false;
        private final java.util.Map<String, String> warnings = new java.util.HashMap<>();
        private final java.util.Map<String, String> errors = new java.util.HashMap<>();

        public void incrementCompeticoes() { competicoesSincronizadas++; }
        public void incrementTemporadas() { temporadasSincronizadas++; }
        public void incrementTimes() { timesSincronizados++; }
        public void setDurationMs(long ms) { this.durationMs = ms; }
        public void setApiAvailable(boolean available) { this.apiAvailable = available; }
        public void setSkipped(boolean skipped) { this.skipped = skipped; }
        public void addWarning(String key, String message) { warnings.put(key, message); }
        public void addError(String key, String message) { errors.put(key, message); }

        public int getCompeticoesSincronizadas() { return competicoesSincronizadas; }
        public int getTemporadasSincronizadas() { return temporadasSincronizadas; }
        public int getTimesSincronizados() { return timesSincronizados; }
        public long getDurationMs() { return durationMs; }
        public boolean isApiAvailable() { return apiAvailable; }
        public boolean isSkipped() { return skipped; }
        public java.util.Map<String, String> getWarnings() { return warnings; }
        public java.util.Map<String, String> getErrors() { return errors; }
    }
}
