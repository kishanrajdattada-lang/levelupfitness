package com.example.gymlog.repository;

import com.example.gymlog.model.WorkoutSet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

    @Query("SELECT COALESCE(SUM(ws.weight * ws.reps * ws.sets), 0.0) FROM WorkoutSet ws WHERE ws.exercise.name = :exerciseName")
    double calculateVolumeByExerciseName(@Param("exerciseName") String exerciseName);

    @Query("SELECT ws FROM WorkoutSet ws JOIN FETCH ws.exercise JOIN FETCH ws.workoutSession ORDER BY ws.id ASC")
    List<WorkoutSet> findAllForGraph();
}
