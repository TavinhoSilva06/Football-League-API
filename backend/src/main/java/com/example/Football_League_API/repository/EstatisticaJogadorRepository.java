package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.EstatisticaJogador;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstatisticaJogadorRepository extends JpaRepository<EstatisticaJogador, Long> {
    Optional<EstatisticaJogador> findByJogadorIdAndTemporadaId(Long jogadorId, Long temporadaId);

    List<EstatisticaJogador> findByTemporadaIdAndJogadorIdIn(Long temporadaId, Collection<Long> jogadorIds);

    @EntityGraph(attributePaths = {"jogador", "temporada"})
    List<EstatisticaJogador> findByTemporadaIdOrderByGolsDesc(Long temporadaId);

    @EntityGraph(attributePaths = {"jogador", "temporada"})
    List<EstatisticaJogador> findByTemporadaIdOrderByAssistenciasDesc(Long temporadaId);

    @Query("""
            select e from EstatisticaJogador e
            join fetch e.jogador
            join fetch e.temporada
            where e.temporada.id = :temporadaId and e.gols > 0
            order by e.gols desc, e.jogos desc, e.jogador.nome asc
            """)
    List<EstatisticaJogador> findArtilharia(@Param("temporadaId") Long temporadaId, Pageable pageable);

    @Query("""
            select e from EstatisticaJogador e
            join fetch e.jogador
            join fetch e.temporada
            where e.temporada.id = :temporadaId and e.assistencias > 0
            order by e.assistencias desc, e.jogos desc, e.jogador.nome asc
            """)
    List<EstatisticaJogador> findAssistencias(@Param("temporadaId") Long temporadaId, Pageable pageable);

    @Query("""
            select e from EstatisticaJogador e
            join fetch e.jogador
            join fetch e.temporada
            where e.temporada.id = :temporadaId
              and (coalesce(e.cartoesAmarelos, 0) > 0 or coalesce(e.cartoesVermelhos, 0) > 0)
            order by (coalesce(e.cartoesAmarelos, 0) + coalesce(e.cartoesVermelhos, 0)) desc,
                     coalesce(e.cartoesVermelhos, 0) desc, e.jogador.nome asc
            """)
    List<EstatisticaJogador> findCartoes(@Param("temporadaId") Long temporadaId, Pageable pageable);
}
