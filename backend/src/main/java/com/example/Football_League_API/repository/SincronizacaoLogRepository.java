package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.SincronizacaoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SincronizacaoLogRepository extends JpaRepository<SincronizacaoLog, Long> {

    Optional<SincronizacaoLog> findFirstByTipoOrderByDataSyncDesc(String tipo);

    @Query("SELECT s FROM SincronizacaoLog s WHERE s.tipo = :tipo " +
           "AND s.dataSync >= :dataLimite " +
           "AND s.status = 'SUCESSO' " +
           "ORDER BY s.dataSync DESC")
    List<SincronizacaoLog> findLastSuccessfulSync(@Param("tipo") String tipo,
                                                  @Param("dataLimite") LocalDateTime dataLimite);
}
