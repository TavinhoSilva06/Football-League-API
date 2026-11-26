package com.example.Football_League_API.event;

import java.time.LocalDateTime;

public record PrazoPalpiteDefinidoEvent(Long temporadaId, Integer rodada, LocalDateTime dataLimite) {
}
