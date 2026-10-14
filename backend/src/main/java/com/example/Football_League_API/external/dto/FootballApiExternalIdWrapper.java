package com.example.Football_League_API.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FootballApiExternalIdWrapper {
    private Integer externalId;
    private String externalSource;
    private Long internalId;

    public static final String SOURCE = "api-football";
}
