package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.Participacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipacaoRepository extends JpaRepository<Participacao, Long> {
    List<Participacao> findByTemporadaId(Long temporadaId);
    Optional<Participacao> findByTemporadaIdAndTimeId(Long temporadaId, Long timeId);
}
