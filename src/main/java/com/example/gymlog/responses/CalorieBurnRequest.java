package com.example.gymlog.responses;

/**
 * Request payload for the calorie burn calculation endpoint.
 *
 * @param exerciseName  Name of the exercise (e.g. "Bench Press")
 * @param weight        Weight lifted per set (kg)
 * @param reps          Number of repetitions per set
 * @param sets          Number of sets performed
 * @param bodyWeightKg  Athlete's body weight in kilograms (used in MET formula)
 */
public record CalorieBurnRequest(
        String exerciseName,
        double weight,
        int reps,
        int sets,
        double bodyWeightKg
) {}
