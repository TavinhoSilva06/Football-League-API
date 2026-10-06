package com.example.Football_League_API.entity;

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
import com.example.Football_League_API.enu.TipoEvento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "eventos_partida")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoPartida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "partida_id")
    private Partida partida;

    @ManyToOne(optional = false)
    @JoinColumn(name = "jogador_id")
    private Jogador jogador;

    @ManyToOne(optional = false)
    @JoinColumn(name = "temporada_id")
    private Temporada temporada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvento tipo;

    @Column
    private Integer minuto;

    @Column
    private String descricao;
}
