package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.EstatisticaJogador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstatisticaJogadorRepository extends JpaRepository<EstatisticaJogador, Long> {
    Optional<EstatisticaJogador> findByJogadorIdAndTemporadaId(Long jogadorId, Long temporadaId);
    List<EstatisticaJogador> findByTemporadaIdOrderByGolsDesc(Long temporadaId);
    List<EstatisticaJogador> findByTemporadaIdOrderByAssistenciasDesc(Long temporadaId);
}
