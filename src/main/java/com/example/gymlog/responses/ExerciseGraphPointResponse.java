package com.example.gymlog.responses;

import java.time.LocalDate;

public record ExerciseGraphPointResponse(
        Long id,
        String exerciseName,
        LocalDate workoutDate,
        double weightKg,
        int reps,
        int sets,
        int totalReps,
        double volumeKg,
        double bodyWeightKg,
        double estimatedCalories,
        double durationMinutes
) {}
