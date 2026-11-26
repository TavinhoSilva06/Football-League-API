package com.example.Football_League_API.external.dto;

import com.example.Football_League_API.enu.PosicaoJogador;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Mapeia um item de GET /players: { "player": {...}, "statistics": [ { "team": {...}, "games": {...} } ] }
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootballApiPlayerDto {

    @JsonProperty("player")
    private PlayerInfo player;

    @JsonProperty("statistics")
    private List<Statistic> statistics;

    public Integer getId() {
        return player != null ? player.getId() : null;
    }

    public boolean isValid() {
        return getId() != null && getOrDefaultName() != null;
    }

    public String getOrDefaultName() {
        String name = player != null ? player.getName() : null;
        return name != null && !name.trim().isEmpty() ? name : "N/A";
    }

    public String getOrDefaultNationality() {
        String nationality = player != null ? player.getNationality() : null;
        return nationality != null && !nationality.trim().isEmpty() ? nationality : null;
    }

    public String getBirthDate() {
        return player != null && player.getBirth() != null ? player.getBirth().getDate() : null;
    }

    // Posição e número da camisa vêm da estatística do time em que o jogador atua
    public PosicaoJogador getPosicao(Integer teamId) {
        String position = statisticFor(teamId)
                .map(s -> s.getGames() != null ? s.getGames().getPosition() : null)
                .orElse(null);

        if (position == null) {
            return null;
        }

        return switch (position) {
            case "Goalkeeper" -> PosicaoJogador.GOLEIRO;
            case "Defender" -> PosicaoJogador.DEFESA;
            case "Midfielder" -> PosicaoJogador.MEIO_CAMPO;
            case "Attacker" -> PosicaoJogador.ATACANTE;
            default -> null;
        };
    }

    public Integer getNumero(Integer teamId) {
        return statisticFor(teamId)
                .map(s -> s.getGames() != null ? s.getGames().getNumber() : null)
                .orElse(null);
    }

    // Minutos jogados pelo jogador no time, na liga informada (somando as partidas da liga)
    public int getMinutos(Integer teamId, Integer leagueId) {
        if (statistics == null) {
            return 0;
        }
        return statistics.stream()
                .filter(s -> s.getTeam() != null && teamId.equals(s.getTeam().getId()))
                .filter(s -> s.getLeague() != null && leagueId.equals(s.getLeague().getId()))
                .filter(s -> s.getGames() != null && s.getGames().getMinutes() != null)
                .mapToInt(s -> s.getGames().getMinutes())
                .sum();
    }

    // Um jogador pode ter estatísticas de mais de um time na mesma temporada (transferências)
    private java.util.Optional<Statistic> statisticFor(Integer teamId) {
        if (statistics == null || statistics.isEmpty()) {
            return java.util.Optional.empty();
        }
        return statistics.stream()
                .filter(s -> s.getTeam() != null && teamId != null && teamId.equals(s.getTeam().getId()))
                .findFirst()
                .or(() -> java.util.Optional.of(statistics.get(0)));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerInfo {

        @JsonProperty("id")
        private Integer id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("nationality")
        private String nationality;

        @JsonProperty("birth")
        private Birth birth;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Birth {

        @JsonProperty("date")
        private String date;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Statistic {

        @JsonProperty("team")
        private TeamRef team;

        @JsonProperty("league")
        private LeagueRef league;

        @JsonProperty("games")
        private Games games;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueRef {

        @JsonProperty("id")
        private Integer id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamRef {

        @JsonProperty("id")
        private Integer id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Games {

        @JsonProperty("number")
        private Integer number;

        @JsonProperty("position")
        private String position;

        @JsonProperty("minutes")
        private Integer minutes;
    }
}
