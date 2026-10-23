package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.Jogador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JogadorRepository extends JpaRepository<Jogador, Long> {
    List<Jogador> findByTimeId(Long timeId);
    Optional<Jogador> findByApiId(Integer apiId);
}
