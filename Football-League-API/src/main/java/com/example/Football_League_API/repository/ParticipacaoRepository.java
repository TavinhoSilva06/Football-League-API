package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.Participacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipacaoRepository extends JpaRepository<Participacao, Long> {
}
