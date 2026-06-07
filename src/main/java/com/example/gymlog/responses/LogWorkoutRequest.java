package com.example.gymlog.responses;

import java.time.LocalDate;

/**
 * Request payload for logging a workout set.
 *
 * @param exerciseName  Name of the exercise
 * @param weight        Weight lifted (kg)
 * @param reps          Repetitions per set
 * @param sets          Number of sets
 * @param bodyWeightKg  Athlete body weight (kg). Optional — defaults to 75 kg when null.
 * @param workoutDate   Date the workout was performed. Optional — defaults to today.
 */
public record LogWorkoutRequest(
        String  exerciseName,
        double  weight,
        int     reps,
        int     sets,
        Double  bodyWeightKg,   // nullable; use effectiveBodyWeight() for a safe value
        LocalDate workoutDate
) {
    public LogWorkoutRequest {
        System.out.println("LogWorkoutRequest received: " + exerciseName);
    }

    /** Returns the supplied body weight, or 75 kg if not provided. */
    public double effectiveBodyWeight() {
        return (bodyWeightKg != null && bodyWeightKg >= 1) ? bodyWeightKg : 75.0;
    }

    /** Returns the supplied workout date, or today if not provided. */
    public LocalDate effectiveWorkoutDate() {
        return workoutDate != null ? workoutDate : LocalDate.now();
    }
}
