package com.example.gymlog.service;

import com.example.gymlog.model.Exercise;
import com.example.gymlog.model.WorkoutSession;
import com.example.gymlog.model.WorkoutSet;
import com.example.gymlog.repository.ExerciseRepository;
import com.example.gymlog.repository.WorkoutSessionRepository;
import com.example.gymlog.repository.WorkoutSetRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutSetRepository workoutSetRepository;

    @Transactional(readOnly = true)
    public double calculateVolume(String exerciseName) {
        return workoutSetRepository.calculateVolumeByExerciseName(exerciseName);
    }

    @Transactional
    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    @Transactional
    public WorkoutSession createWorkoutSession(WorkoutSession session) {
        return workoutSessionRepository.save(session);
    }

    @Transactional
    public WorkoutSet createWorkoutSet(WorkoutSet set) {
        return workoutSetRepository.save(set);
    }

    @Transactional
    public WorkoutSet logWorkout(String exerciseName, double weight, int reps, int sets) {
        return logWorkout(exerciseName, weight, reps, sets, LocalDate.now(), null, null, null);
    }

    @Transactional
    public WorkoutSet logWorkout(
            String exerciseName,
            double weight,
            int reps,
            int sets,
            LocalDate workoutDate,
            Double bodyWeightKg,
            Double estimatedCalories,
            Double durationMinutes) {
        Exercise exercise = exerciseRepository.findByName(exerciseName)
                .orElseGet(() -> exerciseRepository.save(
                        Exercise.builder()
                                .name(exerciseName)
                                .muscleGroup("General")
                                .build()
                ));

        LocalDate effectiveDate = workoutDate != null ? workoutDate : LocalDate.now();

        WorkoutSession session = workoutSessionRepository.findByDate(effectiveDate)
                .orElseGet(() -> workoutSessionRepository.save(
                        WorkoutSession.builder()
                                .date(effectiveDate)
                                .note("Daily Workout Session")
                                .build()
                ));

        WorkoutSet workoutSet = WorkoutSet.builder()
                .workoutSession(session)
                .exercise(exercise)
                .weight(weight)
                .reps(reps)
                .sets(sets)
                .bodyWeightKg(bodyWeightKg)
                .estimatedCalories(estimatedCalories)
                .durationMinutes(durationMinutes)
                .build();

        return workoutSetRepository.save(workoutSet);
    }

    @Transactional(readOnly = true)
    public List<WorkoutSet> getWorkoutSetsForGraph() {
        return workoutSetRepository.findAllForGraph();
    }

    @Transactional(readOnly = true)
    public boolean exerciseExists(String name) {
        return exerciseRepository.findByName(name).isPresent();
    }
}

