package com.example.Football_League_API.repository;

import com.example.Football_League_API.entity.Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TimeRepository extends JpaRepository<Time, Long> {
    Optional<Time> findByNome(String nome);
}
