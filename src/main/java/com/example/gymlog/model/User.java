package com.example.gymlog.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer age;

    private Double weight;

    private Double height;

    private String goal; // Fat Loss, Muscle Gain

    private LocalDate joinedDate;

    @OneToMany(mappedBy = "user")
    private List<WorkoutSession> workoutSessions = new ArrayList<>();
}