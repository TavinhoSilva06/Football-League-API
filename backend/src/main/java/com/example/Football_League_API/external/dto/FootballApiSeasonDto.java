package com.example.Football_League_API.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootballApiSeasonDto {

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("start")
    private String start;

    @JsonProperty("end")
    private String end;

    @JsonProperty("current")
    private Boolean current;

    public boolean isValid() {
        return year != null;
    }

    public String getOrDefaultYear() {
        return year != null ? year.toString() : "N/A";
    }
}
