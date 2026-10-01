package com.example.Football_League_API.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estatisticas_jogador", uniqueConstraints = @UniqueConstraint(columnNames = {"jogador_id", "temporada_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstatisticaJogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "jogador_id")
    private Jogador jogador;

    @ManyToOne(optional = false)
    @JoinColumn(name = "temporada_id")
    private Temporada temporada;

    @Column
    private Integer jogos = 0;

    @Column
    private Integer gols = 0;

    @Column
    private Integer assistencias = 0;

    @Column
    private Integer cartoesAmarelos = 0;

    @Column
    private Integer cartoesVermelhos = 0;
}
