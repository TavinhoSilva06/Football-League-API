package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.Partida;
import com.example.Football_League_API.enu.StatusPartida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {
    List<Partida> findByTemporadaId(Long temporadaId);

    Optional<Partida> findFirstByTemporadaIdAndStatusOrderByRodadaAsc(Long temporadaId, StatusPartida status);
    Optional<Partida> findFirstByTemporadaIdAndStatusAndDataLimitePalpiteNotNullOrderByRodadaAsc(
            Long temporadaId, StatusPartida status);

    @Query("""
            select distinct p.temporada.id from Partida p
            where p.status = :status
              and p.dataLimitePalpite is not null
              and p.dataLimitePalpite <= :limite
            """)
    List<Long> findTemporadaIdsComPrazoExpirado(@Param("status") StatusPartida status,
                                                @Param("limite") LocalDateTime limite);

    List<Partida> findByTemporadaIdAndRodada(Long temporadaId, Integer rodada);
    List<Partida> findByTemporadaIdAndStatus(Long temporadaId, StatusPartida status);
    List<Partida> findByTemporadaIdAndRodadaAndStatus(Long temporadaId, Integer rodada, StatusPartida status);
    List<Partida> findByTimeMandanteIdOrTimeVisitanteId(Long timeMandanteId, Long timeVisitanteId);
    List<Partida> findByTimeMandanteIdAndTemporadaId(Long timeMandanteId, Long temporadaId);
    List<Partida> findByTimeVisitanteIdAndTemporadaId(Long timeVisitanteId, Long temporadaId);
}
