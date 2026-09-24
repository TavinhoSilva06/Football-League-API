package com.example.Football_League_API.entity;

import com.example.Football_League_API.enu.StatusPartida;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "partidas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "temporada_id")
    private Temporada temporada;

    @Column(nullable = false)
    private Integer rodada;

    @Column
    private LocalDateTime dataHora;

    @ManyToOne(optional = false)
    @JoinColumn(name = "time_mandante_id")
    private Time timeMandante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "time_visitante_id")
    private Time timeVisitante;

    @Column
    private Integer golsMandante;

    @Column
    private Integer golsVisitante;

    @Column
    @Enumerated(EnumType.STRING)
    private StatusPartida status;

    @Column
    private String local;
}
