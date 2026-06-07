package com.example.gymlog.config;

import com.example.gymlog.model.Exercise;
import com.example.gymlog.model.WorkoutSession;
import com.example.gymlog.model.WorkoutSet;
import com.example.gymlog.service.WorkoutService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final WorkoutService workoutService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");

        if (workoutService.exerciseExists("ChestPress")) {
            log.info("Database is already seeded with ChestPress. Skipping initialization.");
            return;
        }

        // Create exercise ChestPress

        Exercise chestPress = Exercise.builder()
                .name("ChestPress")
                .muscleGroup("Chest")
                .build();
        chestPress = workoutService.createExercise(chestPress);

        // Create a workout session
        WorkoutSession session = WorkoutSession.builder()
                .date(LocalDate.now())
                .note("Data initializer session")
                .build();
        session = workoutService.createWorkoutSession(session);

        // Create and save 3 sets:
        // 1. 20kg x 10 reps x 3 sets
        WorkoutSet set1 = WorkoutSet.builder()
                .workoutSession(session)
                .exercise(chestPress)
                .weight(20.0)
                .reps(10)
                .sets(3)
                .build();
        workoutService.createWorkoutSet(set1);

        // 2. 25kg x 8 reps x 3 sets
        WorkoutSet set2 = WorkoutSet.builder()
                .workoutSession(session)
                .exercise(chestPress)
                .weight(25.0)
                .reps(8)
                .sets(3)
                .build();
        workoutService.createWorkoutSet(set2);

        // 3. 30kg x 5 reps x 3 sets
        WorkoutSet set3 = WorkoutSet.builder()
                .workoutSession(session)
                .exercise(chestPress)
                .weight(30.0)
                .reps(5)
                .sets(3)
                .build();
        workoutService.createWorkoutSet(set3);

        log.info("Data initialization complete. Volume calculated for ChestPress: {}", 
                workoutService.calculateVolume("ChestPress"));
    }
}
