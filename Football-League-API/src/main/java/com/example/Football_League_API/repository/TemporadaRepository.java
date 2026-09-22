package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.Temporada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TemporadaRepository extends JpaRepository<Temporada, Long> {
    List<Temporada> findByCampeonatoId(Long campeonatoId);

    Optional<Temporada> findByCampeonatoIdAndNome(Long campeonatoId, String nome);
}
