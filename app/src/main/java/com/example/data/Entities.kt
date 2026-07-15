package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey val id: Int = 1,
    val name: String = "User Sehat",
    val age: Int = 22,
    val gender: String = "Male",
    val height: Float = 175f,
    val weight: Float = 70f,
    val targetWeight: Float = 75f,
    val activityLevel: String = "Moderate",
    val fitnessGoal: String = "Build Muscle"
)

@Entity(tableName = "workout_log")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val exerciseName: String,
    val weightKg: Float,
    val sets: Int,
    val reps: Int,
    val durationMinutes: Int,
    val caloriesBurned: Int
)

@Entity(tableName = "weight_log")
data class WeightLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val weightKg: Float
)

@Entity(tableName = "water_log")
data class WaterLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val amountMl: Int
)

@Entity(tableName = "food_log")
data class FoodLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val foodName: String,
    val calories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatsGrams: Float
)

@Entity(tableName = "sleep_log")
data class SleepLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val sleepTime: String,
    val wakeTime: String,
    val durationHours: Float,
    val qualityScore: Int // 1-100
)

@Entity(tableName = "habit_log")
data class HabitLog(
    @PrimaryKey val date: String, // YYYY-MM-DD (One per day)
    val workoutChecked: Boolean = false,
    val waterChecked: Boolean = false,
    val proteinChecked: Boolean = false,
    val sleepChecked: Boolean = false,
    val stretchingChecked: Boolean = false,
    val cardioChecked: Boolean = false
)

@Entity(tableName = "progress_photo")
data class ProgressPhoto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val imageUri: String,
    val tag: String // Front, Side, Back
)

@Entity(tableName = "reminder_settings")
data class ReminderSettings(
    @PrimaryKey val id: Int = 1,
    val workoutEnabled: Boolean = true,
    val workoutTime: String = "07:00",
    val waterEnabled: Boolean = true,
    val waterIntervalHours: Int = 2,
    val mealEnabled: Boolean = true,
    val mealTimes: String = "08:00,13:00,19:00",
    val sleepEnabled: Boolean = true,
    val sleepTime: String = "22:00",
    val weightEnabled: Boolean = true,
    val weightTime: String = "08:00"
)

@Entity(tableName = "exercise_library")
data class ExerciseLibrary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // Chest, Back, Shoulder, Leg, Biceps, Triceps, Core
    val description: String,
    val targetedMuscle: String,
    val defaultReps: String = "10-12",
    val defaultSets: Int = 4,
    val defaultDuration: String = "45s rest",
    val caloriesBurnedPerSet: Int = 15
)

@Entity(tableName = "ai_coach_log")
data class AiCoachLog(
    @PrimaryKey val id: Int = 1,
    val workoutRecommendation: String = "",
    val foodRecognitionResult: String = ""
)

