package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.EventoPartida;
import com.example.Football_League_API.enu.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventoPartidaRepository extends JpaRepository<EventoPartida, Long> {
    List<EventoPartida> findByPartidaId(Long partidaId);
    List<EventoPartida> findByJogadorIdAndTemporadaId(Long jogadorId, Long temporadaId);
    List<EventoPartida> findByTemporadaIdAndTipo(Long temporadaId, TipoEvento tipo);
    List<EventoPartida> findByJogadorIdAndTemporadaIdAndTipo(Long jogadorId, Long temporadaId, TipoEvento tipo);
}
