package com.example.gymlog.repository;

import com.example.gymlog.model.WorkoutSession;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    Optional<WorkoutSession> findByDate(LocalDate date);
}

