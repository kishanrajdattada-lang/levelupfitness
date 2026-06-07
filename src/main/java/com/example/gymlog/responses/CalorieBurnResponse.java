package com.example.gymlog.responses;

/**
 * Response payload containing the estimated calorie burn for an exercise session.
 *
 * @param exerciseName      Name of the exercise
 * @param sets              Number of sets performed
 * @param reps              Repetitions per set
 * @param weightKg          Weight lifted per set (kg)
 * @param bodyWeightKg      Athlete's body weight (kg)
 * @param estimatedCalories Estimated kilocalories burned during the exercise
 * @param durationMinutes   Estimated active exercise duration in minutes
 */
public record CalorieBurnResponse(
        String exerciseName,
        int sets,
        int reps,
        double weightKg,
        double bodyWeightKg,
        double estimatedCalories,
        double durationMinutes
) {}
