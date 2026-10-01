package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.Campeonato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CampeonatoRepository extends JpaRepository<Campeonato, Long> {
    Optional<Campeonato> findByNome(String nome);
}
