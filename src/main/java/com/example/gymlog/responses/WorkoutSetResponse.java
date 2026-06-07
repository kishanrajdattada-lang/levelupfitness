package com.example.gymlog.responses;

import java.time.LocalDate;

public record WorkoutSetResponse(
    Long id,
    String exerciseName,
    double weight,
    int reps,
    int sets,
    LocalDate workoutDate,
    String sessionNote
) {
}
