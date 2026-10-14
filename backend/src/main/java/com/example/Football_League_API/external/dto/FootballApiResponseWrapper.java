package com.example.Football_League_API.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootballApiResponseWrapper<T> {

    @JsonProperty("response")
    private T response;

    @JsonProperty("errors")
    private Object errors;

    public boolean hasErrors() {
        if (errors == null) {
            return false;
        }
        // A API-Sports envia "errors": [] ou "errors": {} quando não há erro
        if (errors instanceof Collection<?> collection) {
            return !collection.isEmpty();
        }
        if (errors instanceof Map<?, ?> map) {
            return !map.isEmpty();
        }
        return true;
    }

    public boolean isValidResponse() {
        return response != null && !hasErrors();
    }
}
