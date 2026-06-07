package com.example.gymlog.responses;

import java.time.LocalDate;

/**
 * Combined response returned by the "log + auto-calorie" endpoint.
 * Bundles the saved workout details together with an instant calorie estimate
 * so the UI can display both without a second round-trip.
 *
 * @param id                 Database ID of the persisted WorkoutSet
 * @param exerciseName       Name of the exercise
 * @param weight             Weight lifted (kg)
 * @param reps               Reps per set
 * @param sets               Number of sets
 * @param workoutDate        Date the workout was performed
 * @param sessionNote        Note from the workout session
 * @param bodyWeightKg       Athlete body weight used for calorie estimation
 * @param estimatedCalories  Estimated kilocalories burned
 * @param durationMinutes    Estimated active exercise duration (minutes)
 */
public record WorkoutLogResponse(
        Long   id,
        String exerciseName,
        double weight,
        int    reps,
        int    sets,
        LocalDate workoutDate,
        String sessionNote,
        double bodyWeightKg,
        double estimatedCalories,
        double durationMinutes
) {}
