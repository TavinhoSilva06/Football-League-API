package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeRepository extends JpaRepository<Time, Long> {
    Optional<Time> findByNome(String nome);

    /**
     * Busca todos os times que participam de um campeonato (em qualquer temporada).
     * Query: Time → Participacao → Temporada → Campeonato
     */
    @Query("SELECT DISTINCT t FROM Time t " +
           "INNER JOIN Participacao p ON t.id = p.time.id " +
           "INNER JOIN Temporada temp ON p.temporada.id = temp.id " +
           "WHERE temp.campeonato.id = :campeonatoId " +
           "ORDER BY t.nome ASC")
    List<Time> findByCampeonatoId(@Param("campeonatoId") Long campeonatoId);
}
