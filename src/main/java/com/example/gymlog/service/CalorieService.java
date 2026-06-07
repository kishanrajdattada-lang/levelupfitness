package com.example.gymlog.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service responsible for estimating calorie burn for resistance/strength exercises.
 *
 * <p><b>Formula (MET-based):</b><br>
 * {@code Calories (kcal) = MET × bodyWeightKg × durationHours}
 *
 * <p><b>Duration estimation:</b><br>
 * Active time is approximated as: {@code sets × reps × TIME_PER_REP_SECONDS}
 * plus rest time between sets: {@code (sets - 1) × REST_BETWEEN_SETS_SECONDS}
 *
 * <p>MET values are drawn from the 2011 Compendium of Physical Activities.
 * Exercises not in the lookup table default to a general resistance-training MET of 5.0.
 */
@Service
public class CalorieService {

    /**
     * Average seconds of time-under-tension per repetition (eccentric + concentric).
     */
    private static final double TIME_PER_REP_SECONDS = 4.0;

    /**
     * Typical rest time between sets in seconds (60 s assumed).
     */
    private static final double REST_BETWEEN_SETS_SECONDS = 60.0;

    /**
     * Default MET for general strength / weight training (moderate effort).
     */
    private static final double DEFAULT_MET = 5.0;

    /**
     * MET lookup table keyed by lowercase exercise name.
     * Sources: 2011 Compendium of Physical Activities.
     */
    private static final Map<String, Double> MET_TABLE = Map.ofEntries(
            Map.entry("bench press",         5.0),
            Map.entry("squat",               5.0),
            Map.entry("deadlift",            6.0),
            Map.entry("overhead press",      4.5),
            Map.entry("barbell row",         5.0),
            Map.entry("pull up",             8.0),
            Map.entry("chin up",             8.0),
            Map.entry("push up",             3.8),
            Map.entry("bicep curl",          3.5),
            Map.entry("tricep extension",    3.5),
            Map.entry("lat pulldown",        4.5),
            Map.entry("leg press",           5.0),
            Map.entry("leg curl",            4.0),
            Map.entry("leg extension",       4.0),
            Map.entry("calf raise",          3.0),
            Map.entry("plank",               3.5),
            Map.entry("crunch",              3.0),
            Map.entry("dumbbell row",        5.0),
            Map.entry("shoulder press",      4.5),
            Map.entry("hip thrust",          5.0),
            Map.entry("romanian deadlift",   5.5),
            Map.entry("cable fly",           4.0),
            Map.entry("chest fly",           4.0),
            Map.entry("face pull",           3.5),
            Map.entry("running",            11.5),
            Map.entry("cycling",             8.0),
            Map.entry("jumping jacks",       8.0),
            Map.entry("burpee",             10.0)
    );

    /**
     * Estimates the kilocalories burned for a single exercise session.
     *
     * @param exerciseName  Name of the exercise
     * @param sets          Number of sets performed
     * @param reps          Repetitions per set
     * @param bodyWeightKg  Athlete's body weight in kilograms
     * @return estimated kcal burned (rounded to 2 decimal places)
     */
    public double estimateCalories(String exerciseName, int sets, int reps, double bodyWeightKg) {
        double met = resolveMet(exerciseName);
        double durationSeconds = computeDurationSeconds(sets, reps);
        double durationHours = durationSeconds / 3600.0;
        double calories = met * bodyWeightKg * durationHours;
        return Math.round(calories * 100.0) / 100.0;
    }

    /**
     * Returns the estimated active exercise duration in minutes.
     */
    public double estimateDurationMinutes(int sets, int reps) {
        return Math.round((computeDurationSeconds(sets, reps) / 60.0) * 100.0) / 100.0;
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private double resolveMet(String exerciseName) {
        if (exerciseName == null || exerciseName.isBlank()) {
            return DEFAULT_MET;
        }
        return MET_TABLE.getOrDefault(exerciseName.trim().toLowerCase(), DEFAULT_MET);
    }

    /**
     * Total time = (active lifting time) + (rest between sets).
     * We only count one rest interval per completed set (i.e. sets-1 rests).
     */
    private double computeDurationSeconds(int sets, int reps) {
        double activeSeconds = (double) sets * reps * TIME_PER_REP_SECONDS;
        double restSeconds = Math.max(0, sets - 1) * REST_BETWEEN_SETS_SECONDS;
        return activeSeconds + restSeconds;
    }
}
