package com.example.Football_League_API.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PalpiteLoteRequestDto {

    @Valid
    @NotEmpty(message = "Informe ao menos um palpite")
    private List<PalpiteRequestDto> palpites;
}
