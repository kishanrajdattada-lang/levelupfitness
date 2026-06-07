package com.example.gymlog;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.gymlog.model.Exercise;
import com.example.gymlog.model.WorkoutSession;
import com.example.gymlog.model.WorkoutSet;
import com.example.gymlog.repository.ExerciseRepository;
import com.example.gymlog.repository.WorkoutSessionRepository;
import com.example.gymlog.repository.WorkoutSetRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class GymLogApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    @Autowired
    private WorkoutSetRepository workoutSetRepository;

    @BeforeEach
    void setUp() {
        workoutSetRepository.deleteAll();
        workoutSessionRepository.deleteAll();
        exerciseRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        // Basic context loading check
    }

    @Test
    void getProgress_shouldReturnCorrectVolume() throws Exception {
        // Arrange
        Exercise squat = Exercise.builder()
                .name("Squat")
                .muscleGroup("Legs")
                .build();
        squat = exerciseRepository.save(squat);

        WorkoutSession session = WorkoutSession.builder()
                .date(LocalDate.of(2026, 5, 31))
                .note("Leg day")
                .build();
        session = workoutSessionRepository.save(session);

        // Save sets
        WorkoutSet set1 = WorkoutSet.builder()
                .workoutSession(session)
                .exercise(squat)
                .weight(100.0)
                .reps(5)
                .sets(3)
                .build();

        WorkoutSet set2 = WorkoutSet.builder()
                .workoutSession(session)
                .exercise(squat)
                .weight(120.0)
                .reps(5)
                .sets(2)
                .build();

        workoutSetRepository.save(set1);
        workoutSetRepository.save(set2);

        // Act & Assert
        // Squat: (100.0 * 5 * 3) + (120.0 * 5 * 2) = 1500.0 + 1200.0 = 2700.0
        mockMvc.perform(get("/api/progress/{exerciseName}", "Squat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseName", is("Squat")))
                .andExpect(jsonPath("$.totalVolume", is(2700.0)));
    }

    @Test
    void getProgress_shouldReturnZeroVolume_whenExerciseDoesNotExistOrHasNoSets() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/progress/{exerciseName}", "Deadlift"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseName", is("Deadlift")))
                .andExpect(jsonPath("$.totalVolume", is(0.0)));
    }

    @Test
    void logWorkout_shouldSaveWorkoutAndReturnResponse() throws Exception {
        String jsonPayload = """
                {
                  "exerciseName": "OverheadPress",
                  "weight": 40.0,
                  "reps": 8,
                  "sets": 3
                }
                """;

        // Act & Assert POST request
        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.exerciseName", is("OverheadPress")))
                .andExpect(jsonPath("$.weight", is(40.0)))
                .andExpect(jsonPath("$.reps", is(8)))
                .andExpect(jsonPath("$.sets", is(3)));

        // Verify volume endpoint reflects the newly logged workout
        // OverheadPress volume: 40.0 * 8 * 3 = 960.0
        mockMvc.perform(get("/api/progress/{exerciseName}", "OverheadPress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseName", is("OverheadPress")))
                .andExpect(jsonPath("$.totalVolume", is(960.0)));
    }
}

