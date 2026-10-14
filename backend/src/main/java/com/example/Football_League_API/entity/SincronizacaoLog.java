package com.example.Football_League_API.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sincronizacoes_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SincronizacaoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private LocalDateTime dataSync;

    @Column
    private Integer quantidadeSincronizada;

    @Column
    private String status;

    @Column(columnDefinition = "TEXT")
    private String mensagemErro;
}
