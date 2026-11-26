package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.Palpite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PalpiteRepository extends JpaRepository<Palpite, Long> {
    Optional<Palpite> findByUsuarioIdAndPartidaId(Long usuarioId, Long partidaId);

    List<Palpite> findByPartidaId(Long partidaId);

    List<Palpite> findByPartida_Temporada_IdAndPartida_Rodada(Long temporadaId, Integer rodada);

    List<Palpite> findByUsuarioIdAndPartida_Temporada_Id(Long usuarioId, Long temporadaId);

    List<Palpite> findByUsuarioIdAndPartida_Temporada_IdAndPartida_Rodada(Long usuarioId, Long temporadaId, Integer rodada);
}
