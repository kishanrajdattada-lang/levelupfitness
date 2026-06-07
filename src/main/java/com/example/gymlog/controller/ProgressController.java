package com.example.gymlog.controller;

import com.example.gymlog.model.WorkoutSet;
import com.example.gymlog.responses.*;
import com.example.gymlog.service.CalorieService;
import com.example.gymlog.service.WorkoutService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProgressController {

    private static final double DEFAULT_BODY_WEIGHT_KG = 75.0;

    private final WorkoutService workoutService;
    private final CalorieService calorieService;

    @GetMapping("/progress/{exerciseName}")
    public ResponseEntity<VolumeResponse> getProgress(@PathVariable String exerciseName) {
        double totalVolume = workoutService.calculateVolume(exerciseName);
        return ResponseEntity.ok(new VolumeResponse(exerciseName, totalVolume));
    }

    @GetMapping("/workouts/graph")
    public ResponseEntity<WorkoutGraphResponse> getWorkoutGraph() {
        return ResponseEntity.ok(buildWorkoutGraph());
    }

    @GetMapping("/calories/by-exercise")
    public ResponseEntity<List<ExerciseGraphSummaryResponse>> getCaloriesByExercise() {
        return ResponseEntity.ok(buildWorkoutGraph().exercises());
    }

    @PostMapping("/workouts")
    public ResponseEntity<WorkoutSetResponse> logWorkout(@RequestBody LogWorkoutRequest request) {
        double effectiveBw = request.effectiveBodyWeight();
        double calories = calorieService.estimateCalories(
                request.exerciseName(), request.sets(), request.reps(), effectiveBw);
        double durationMins = calorieService.estimateDurationMinutes(request.sets(), request.reps());

        WorkoutSet savedSet = workoutService.logWorkout(
                request.exerciseName(),
                request.weight(),
                request.reps(),
                request.sets(),
                request.effectiveWorkoutDate(),
                effectiveBw,
                calories,
                durationMins
        );

        WorkoutSetResponse response = new WorkoutSetResponse(
                savedSet.getId(),
                savedSet.getExercise().getName(),
                savedSet.getWeight(),
                savedSet.getReps(),
                savedSet.getSets(),
                savedSet.getWorkoutSession().getDate(),
                savedSet.getWorkoutSession().getNote()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Logs a workout set AND automatically estimates calorie burn in a single call.
     *
     * <p>The {@code bodyWeightKg} field in the request is optional; when omitted or
     * {@code null}, a default of 75 kg is used for the MET-based calorie formula.
     *
     * <p>Example request body:
     * <pre>
     * {
     *   "exerciseName": "Bench Press",
     *   "weight": 80,
     *   "reps": 10,
     *   "sets": 4,
     *   "bodyWeightKg": 75
     * }
     * </pre>
     *
     * @param request exercise details; bodyWeightKg is optional (defaults to 75 kg)
     * @return saved workout data plus instant calorie &amp; duration estimates
     */
    @PostMapping("/workouts/log")
    public ResponseEntity<WorkoutLogResponse> logWorkoutWithCalories(
            @RequestBody LogWorkoutRequest request) {

        double effectiveBw   = request.effectiveBodyWeight();
        double calories      = calorieService.estimateCalories(
                request.exerciseName(), request.sets(), request.reps(), effectiveBw);
        double durationMins  = calorieService.estimateDurationMinutes(
                request.sets(), request.reps());

        WorkoutSet savedSet = workoutService.logWorkout(
                request.exerciseName(),
                request.weight(),
                request.reps(),
                request.sets(),
                request.effectiveWorkoutDate(),
                effectiveBw,
                calories,
                durationMins
        );

        WorkoutLogResponse response = new WorkoutLogResponse(
                savedSet.getId(),
                savedSet.getExercise().getName(),
                savedSet.getWeight(),
                savedSet.getReps(),
                savedSet.getSets(),
                savedSet.getWorkoutSession().getDate(),
                savedSet.getWorkoutSession().getNote(),
                effectiveBw,
                calories,
                durationMins
        );

        return ResponseEntity.ok(response);
    }

    private WorkoutGraphResponse buildWorkoutGraph() {
        List<ExerciseGraphPointResponse> points = workoutService.getWorkoutSetsForGraph()
                .stream()
                .map(this::toGraphPoint)
                .toList();

        Map<String, List<ExerciseGraphPointResponse>> byExercise = points.stream()
                .collect(Collectors.groupingBy(
                        ExerciseGraphPointResponse::exerciseName,
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<ExerciseGraphSummaryResponse> summaries = byExercise.entrySet()
                .stream()
                .map(entry -> toExerciseSummary(entry.getKey(), entry.getValue()))
                .toList();

        return new WorkoutGraphResponse(points, summaries);
    }

    private ExerciseGraphPointResponse toGraphPoint(WorkoutSet set) {
        String exerciseName = set.getExercise().getName();
        double bodyWeightKg = set.getBodyWeightKg() != null
                ? set.getBodyWeightKg()
                : DEFAULT_BODY_WEIGHT_KG;
        double calories = set.getEstimatedCalories() != null
                ? set.getEstimatedCalories()
                : calorieService.estimateCalories(exerciseName, set.getSets(), set.getReps(), bodyWeightKg);
        double durationMinutes = set.getDurationMinutes() != null
                ? set.getDurationMinutes()
                : calorieService.estimateDurationMinutes(set.getSets(), set.getReps());
        int totalReps = set.getSets() * set.getReps();
        double volumeKg = roundTwo(set.getWeight() * totalReps);

        return new ExerciseGraphPointResponse(
                set.getId(),
                exerciseName,
                set.getWorkoutSession().getDate(),
                set.getWeight(),
                set.getReps(),
                set.getSets(),
                totalReps,
                volumeKg,
                bodyWeightKg,
                roundTwo(calories),
                roundTwo(durationMinutes)
        );
    }

    private ExerciseGraphSummaryResponse toExerciseSummary(
            String exerciseName,
            List<ExerciseGraphPointResponse> points) {
        ExerciseGraphPointResponse latest = points.get(points.size() - 1);
        int totalReps = points.stream().mapToInt(ExerciseGraphPointResponse::totalReps).sum();
        double totalVolumeKg = points.stream().mapToDouble(ExerciseGraphPointResponse::volumeKg).sum();
        double totalCalories = points.stream().mapToDouble(ExerciseGraphPointResponse::estimatedCalories).sum();
        double totalDurationMinutes = points.stream().mapToDouble(ExerciseGraphPointResponse::durationMinutes).sum();

        return new ExerciseGraphSummaryResponse(
                exerciseName,
                latest.weightKg(),
                latest.reps(),
                latest.sets(),
                totalReps,
                roundTwo(totalVolumeKg),
                roundTwo(totalCalories),
                latest.estimatedCalories(),
                roundTwo(totalDurationMinutes),
                latest.workoutDate(),
                points.size()
        );
    }

    private double roundTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }


    /**
     * Estimates the calories burned for a given exercise.
     *
     * <p>Example request body:
     * <pre>
     * {
     *   "exerciseName": "Bench Press",
     *   "weight": 80,
     *   "reps": 10,
     *   "sets": 4,
     *   "bodyWeightKg": 75
     * }
     * </pre>
     *
     * @param request exercise details and athlete body weight
     * @return estimated kcal burned and session duration
     */
    @PostMapping("/calories/burn")
    public ResponseEntity<CalorieBurnResponse> calculateCalorieBurn(
            @RequestBody CalorieBurnRequest request) {

        double calories = calorieService.estimateCalories(
                request.exerciseName(),
                request.sets(),
                request.reps(),
                request.bodyWeightKg()
        );
        double durationMinutes = calorieService.estimateDurationMinutes(
                request.sets(),
                request.reps()
        );

        CalorieBurnResponse response = new CalorieBurnResponse(
                request.exerciseName(),
                request.sets(),
                request.reps(),
                request.weight(),
                request.bodyWeightKg(),
                calories,
                durationMinutes
        );

        return ResponseEntity.ok(response);
    }
}
