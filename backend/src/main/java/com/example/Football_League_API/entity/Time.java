package com.example.Football_League_API.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "times")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column
    private String sigla;

    @Column
    private String pais;

    @Column
    private String cidade;

    @Column
    private String estadio;

    @Column
    private String escudoUrl;

    @OneToMany(mappedBy = "time")
    private List<Jogador> jogadores;

    @OneToMany(mappedBy = "time")
    private List<Participacao> participacoes;
}
