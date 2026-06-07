package com.example.gymlog.responses;

import java.util.List;

public record WorkoutGraphResponse(
        List<ExerciseGraphPointResponse> points,
        List<ExerciseGraphSummaryResponse> exercises
) {}
