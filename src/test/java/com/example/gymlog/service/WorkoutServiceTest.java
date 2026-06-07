package com.example.gymlog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.gymlog.repository.ExerciseRepository;
import com.example.gymlog.repository.WorkoutSessionRepository;
import com.example.gymlog.repository.WorkoutSetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock
    private WorkoutSetRepository workoutSetRepository;

    @InjectMocks
    private WorkoutService workoutService;

    @Test
    void calculateVolume_shouldReturnVolumeFromRepository() {
        // Arrange
        String exerciseName = "Bench Press";
        double expectedVolume = 3000.0;
        when(workoutSetRepository.calculateVolumeByExerciseName(exerciseName)).thenReturn(expectedVolume);

        // Act
        double actualVolume = workoutService.calculateVolume(exerciseName);

        // Assert
        assertEquals(expectedVolume, actualVolume, 0.001);
        verify(workoutSetRepository).calculateVolumeByExerciseName(exerciseName);
    }
}
