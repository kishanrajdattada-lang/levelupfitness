package com.example.gymlog.responses;

import java.time.LocalDate;

public record ExerciseGraphSummaryResponse(
        String exerciseName,
        double latestWeightKg,
        int latestReps,
        int latestSets,
        int totalReps,
        double totalVolumeKg,
        double totalCalories,
        double latestCalories,
        double totalDurationMinutes,
        LocalDate lastWorkoutDate,
        int entries
) {}
