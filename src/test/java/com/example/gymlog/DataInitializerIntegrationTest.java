package com.example.gymlog;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:gymlog-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DataInitializerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getProgress_shouldReturnSeededChestPressVolume() throws Exception {
        // Expected: (20*10*3) + (25*8*3) + (30*5*3) = 600 + 600 + 450 = 1650.0
        mockMvc.perform(get("/api/progress/{exerciseName}", "ChestPress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseName", is("ChestPress")))
                .andExpect(jsonPath("$.totalVolume", is(1650.0)));
    }

    @Test
    void getWorkoutGraph_shouldReturnSeededExerciseCaloriesAndTotals() throws Exception {
        mockMvc.perform(get("/api/workouts/graph"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points.length()", is(3)))
                .andExpect(jsonPath("$.exercises.length()", is(1)))
                .andExpect(jsonPath("$.exercises[0].exerciseName", is("ChestPress")))
                .andExpect(jsonPath("$.exercises[0].totalReps", is(69)))
                .andExpect(jsonPath("$.exercises[0].totalVolumeKg", is(1650.0)))
                .andExpect(jsonPath("$.exercises[0].totalCalories", is(66.25)));
    }

    @Test
    void logWorkoutWithCalories_shouldPersistCaloriesForGraph() throws Exception {
        mockMvc.perform(post("/api/workouts/log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "exerciseName": "Bench Press",
                                  "weight": 80,
                                  "reps": 10,
                                  "sets": 4,
                                  "bodyWeightKg": 80
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estimatedCalories", is(37.78)))
                .andExpect(jsonPath("$.durationMinutes", is(5.67)));

        mockMvc.perform(get("/api/workouts/graph"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exercises.length()", is(2)))
                .andExpect(jsonPath("$.exercises[1].exerciseName", is("Bench Press")))
                .andExpect(jsonPath("$.exercises[1].latestWeightKg", is(80.0)))
                .andExpect(jsonPath("$.exercises[1].totalReps", is(40)))
                .andExpect(jsonPath("$.exercises[1].totalCalories", is(37.78)));
    }
}
