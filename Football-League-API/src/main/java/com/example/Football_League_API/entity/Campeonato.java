package com.example.Football_League_API.entity;

import com.example.Football_League_API.enu.FormatoCampeonato;
import com.example.Football_League_API.enu.TipoCampeonato;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "campeonatos", uniqueConstraints = @UniqueConstraint(columnNames = "nome"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campeonato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column
    private String pais;

    @Column
    @Enumerated(EnumType.STRING)
    private TipoCampeonato tipo;

    @Column
    private String descricao;

    @Column
    @Enumerated(EnumType.STRING)
    private FormatoCampeonato formato;

    @OneToMany(mappedBy = "campeonato")
    private List<Temporada> temporadas;
}
