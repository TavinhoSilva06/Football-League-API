package com.example.Football_League_API.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Mapeia um item de GET /teams: { "team": {...}, "venue": {...} }
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootballApiTeamDto {

    @JsonProperty("team")
    private TeamInfo team;

    public Integer getId() {
        return team != null ? team.getId() : null;
    }

    public String getName() {
        return team != null ? team.getName() : null;
    }

    public String getLogo() {
        return team != null ? team.getLogo() : null;
    }

    public boolean isValid() {
        return getId() != null && getName() != null && !getName().trim().isEmpty();
    }

    public String getOrDefaultName() {
        return getName() != null && !getName().trim().isEmpty() ? getName() : "N/A";
    }

    public String getOrDefaultCode() {
        String code = team != null ? team.getCode() : null;
        return code != null && !code.trim().isEmpty() ? code : "";
    }

    public String getOrDefaultCountry() {
        String country = team != null ? team.getCountry() : null;
        return country != null && !country.trim().isEmpty() ? country : "N/A";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamInfo {

        @JsonProperty("id")
        private Integer id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("code")
        private String code;

        @JsonProperty("country")
        private String country;

        @JsonProperty("founded")
        private Integer founded;

        @JsonProperty("national")
        private Boolean national;

        @JsonProperty("logo")
        private String logo;
    }
}
