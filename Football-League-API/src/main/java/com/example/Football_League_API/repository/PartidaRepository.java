package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.Partida;
import com.example.Football_League_API.enu.StatusPartida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {
    List<Partida> findByTemporadaId(Long temporadaId);
    List<Partida> findByTemporadaIdAndRodada(Long temporadaId, Integer rodada);
    List<Partida> findByTemporadaIdAndStatus(Long temporadaId, StatusPartida status);
    List<Partida> findByTemporadaIdAndRodadaAndStatus(Long temporadaId, Integer rodada, StatusPartida status);
    List<Partida> findByTimeMandanteIdOrTimeVisitanteId(Long timeMandanteId, Long timeVisitanteId);
    List<Partida> findByTimeMandanteIdAndTemporadaId(Long timeMandanteId, Long temporadaId);
    List<Partida> findByTimeVisitanteIdAndTemporadaId(Long timeVisitanteId, Long temporadaId);
}
