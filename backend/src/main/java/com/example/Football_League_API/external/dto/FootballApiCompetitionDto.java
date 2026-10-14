package com.example.Football_League_API.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Mapeia um item de GET /leagues: { "league": {...}, "country": {...}, "seasons": [...] }
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootballApiCompetitionDto {

    @JsonProperty("league")
    private LeagueInfo league;

    @JsonProperty("country")
    private CountryInfo country;

    @JsonProperty("seasons")
    private List<FootballApiSeasonDto> seasons;

    public List<FootballApiSeasonDto> getSeasons() {
        return seasons != null ? seasons : List.of();
    }

    public Integer getId() {
        return league != null ? league.getId() : null;
    }

    public String getName() {
        return league != null ? league.getName() : null;
    }

    public String getType() {
        return league != null ? league.getType() : null;
    }

    public String getLogo() {
        return league != null ? league.getLogo() : null;
    }

    public boolean isValid() {
        return getId() != null && getName() != null && !getName().trim().isEmpty();
    }

    public String getOrDefaultName() {
        return getName() != null && !getName().trim().isEmpty() ? getName() : "N/A";
    }

    public String getOrDefaultCountry() {
        String nome = country != null ? country.getName() : null;
        return nome != null && !nome.trim().isEmpty() ? nome : "N/A";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueInfo {

        @JsonProperty("id")
        private Integer id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("type")
        private String type;

        @JsonProperty("logo")
        private String logo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CountryInfo {

        @JsonProperty("name")
        private String name;

        @JsonProperty("code")
        private String code;

        @JsonProperty("flag")
        private String flag;
    }
}
