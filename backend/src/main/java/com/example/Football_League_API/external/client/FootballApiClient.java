package com.example.Football_League_API.external.client;

import com.example.Football_League_API.external.dto.FootballApiCompetitionDto;
import com.example.Football_League_API.external.dto.FootballApiResponseWrapper;
import com.example.Football_League_API.external.dto.FootballApiSeasonDto;
import com.example.Football_League_API.external.dto.FootballApiTeamDto;
import com.example.Football_League_API.external.exception.FootballApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class FootballApiClient {

    private static final String BASE_URL = "https://v3.football.api-sports.io";
    private static final int MAX_RETRIES = 3;
    private static final int INITIAL_RETRY_DELAY_MS = 2000;

    private final RestTemplate restTemplate;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicInteger dailyRequestLimit = new AtomicInteger(100);

    @Value("${football.api.key:}")
    private String apiKey;

    @Value("${football.api.enabled:true}")
    private boolean apiEnabled;

    public List<FootballApiCompetitionDto> getCompetitions() {
        if (!isApiEnabled()) {
            log.warn("Football API esta desabilitada. Retornando lista vazia.");
            return new ArrayList<>();
        }

        validateApiKey();

        String url = BASE_URL + "/leagues?type=league";
        log.info("Buscando competicoes da API-Football");

        return executeRequestWithRetry(url, "competicoes",
                new ParameterizedTypeReference<FootballApiResponseWrapper<List<FootballApiCompetitionDto>>>() {});
    }

    public List<FootballApiTeamDto> getTeams(Integer leagueId, Integer season) {
        if (!isApiEnabled()) {
            log.warn("Football API esta desabilitada. Retornando lista vazia.");
            return new ArrayList<>();
        }

        if (leagueId == null || leagueId <= 0) {
            log.error("ID de liga invalido: {}", leagueId);
            throw new FootballApiException("ID de liga invalido");
        }

        if (season == null || season <= 0) {
            log.error("Temporada invalida: {}", season);
            throw new FootballApiException("Temporada invalida");
        }

        validateApiKey();

        String url = BASE_URL + "/teams?league=" + leagueId + "&season=" + season;
        log.info("Buscando times para liga: {} - Temporada: {}", leagueId, season);

        return executeRequestWithRetry(url, "times da liga " + leagueId + " temporada " + season,
                new ParameterizedTypeReference<FootballApiResponseWrapper<List<FootballApiTeamDto>>>() {});
    }

    private <T> T executeRequestWithRetry(String url, String requestDescription,
                                          ParameterizedTypeReference<FootballApiResponseWrapper<T>> typeRef) {
        checkRateLimit();

        int retryCount = 0;

        while (retryCount < MAX_RETRIES) {
            try {
                log.debug("Executando requisicao {} - Tentativa {}/{}", requestDescription, retryCount + 1, MAX_RETRIES);

                ResponseEntity<FootballApiResponseWrapper<T>> response =
                        restTemplate.exchange(url, HttpMethod.GET, null, typeRef);
                requestCount.incrementAndGet();

                if (response.getStatusCode() == HttpStatus.OK) {
                    FootballApiResponseWrapper<T> wrapper = response.getBody();

                    if (wrapper != null && wrapper.isValidResponse()) {
                        log.info("Requisicao bem-sucedida: {}", requestDescription);
                        return wrapper.getResponse();
                    } else {
                        log.warn("Resposta invalida ou com erros: {} - erros: {}", requestDescription,
                                wrapper != null ? wrapper.getErrors() : null);
                        throw new FootballApiException("Resposta invalida da API: " + requestDescription);
                    }
                }

                log.warn("Status HTTP nao esperado: {} para {}", response.getStatusCode(), requestDescription);
                throw new FootballApiException("Status HTTP: " + response.getStatusCode());

            } catch (HttpClientErrorException.TooManyRequests e) {
                log.error("Limite de requisicoes atingido (429). Aguardando ate amanha.");
                throw new FootballApiException("Limite de requisicoes atingido. Tente amanha.", e);

            } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
                log.error("Erro de autenticacao (401/403): Chave API invalida ou expirada");
                throw new FootballApiException("Chave API invalida ou nao autorizada", e);

            } catch (ResourceAccessException e) {
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    long delayMs = INITIAL_RETRY_DELAY_MS * (long) Math.pow(2, retryCount - 1);
                    log.warn("Erro de conectividade. Tentando novamente em {}ms (tentativa {}/{})", delayMs, retryCount, MAX_RETRIES);
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new FootballApiException("Interrupcao durante retry", ie);
                    }
                } else {
                    log.error("Falha apos {} tentativas de reconexao: {}", MAX_RETRIES, e.getMessage());
                    throw new FootballApiException("API indisponivel apos " + MAX_RETRIES + " tentativas", e);
                }

            } catch (HttpClientErrorException e) {
                log.error("Erro HTTP: {} - {}", e.getStatusCode(), e.getMessage());
                throw new FootballApiException("Erro HTTP: " + e.getStatusCode(), e);

            } catch (FootballApiException e) {
                throw e;

            } catch (Exception e) {
                log.error("Erro inesperado: {}", e.getMessage(), e);
                throw new FootballApiException("Erro ao chamar API-Football: " + e.getMessage(), e);
            }
        }

        throw new FootballApiException("Falha em todas as tentativas de requisicao");
    }

    private void validateApiKey() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("Variavel de ambiente FOOTBALL_API_KEY nao configurada");
            throw new FootballApiException("Configure a variavel de ambiente FOOTBALL_API_KEY");
        }
    }

    private void checkRateLimit() {
        int currentCount = requestCount.get();
        if (currentCount >= dailyRequestLimit.get()) {
            log.error("Limite de {} requisicoes/dia atingido. Redefina amanha.", dailyRequestLimit.get());
            throw new FootballApiException("Limite de requisicoes diarias atingido. Tente amanha.");
        }
    }

    private boolean isApiEnabled() {
        return apiEnabled;
    }

    public void resetDailyCounter() {
        requestCount.set(0);
        log.info("Contador diario de requisicoes resetado");
    }

    public int getRequestCount() {
        return requestCount.get();
    }

    public int getDailyLimit() {
        return dailyRequestLimit.get();
    }
}
