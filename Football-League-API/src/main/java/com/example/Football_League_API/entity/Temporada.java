package com.example.Football_League_API.entity;

import com.example.Football_League_API.enu.StatusTemporada;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "temporadas", uniqueConstraints = @UniqueConstraint(columnNames = {"campeonato_id", "nome"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Temporada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "campeonato_id")
    private Campeonato campeonato;

    @Column(nullable = false)
    private String nome;

    @Column
    private LocalDate dataInicio;

    @Column
    private LocalDate dataFim;

    @Column
    private Integer numRodadas; // Número máximo de rodadas (ex: 38 para Premier League, 34 para Bundesliga)

    @Column
    @Enumerated(EnumType.STRING)
    private StatusTemporada status;

    @OneToMany(mappedBy = "temporada")
    private List<Participacao> participacoes;

    @OneToMany(mappedBy = "temporada")
    private List<Partida> partidas;
}
