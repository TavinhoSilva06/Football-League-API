package com.example.Football_League_API.entity;

import com.example.Football_League_API.enu.PosicaoJogador;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "jogadores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "time_id")
    private Time time;

    @Column(nullable = false)
    private String nome;

    @Column
    private String nacionalidade;

    @Column
    @Enumerated(EnumType.STRING)
    private PosicaoJogador posicao;

    @Column
    private Integer numeroCamisa;

    @Column
    private LocalDate dataNascimento;

    @OneToMany(mappedBy = "jogador")
    private List<EstatisticaJogador> estatisticas;
}
