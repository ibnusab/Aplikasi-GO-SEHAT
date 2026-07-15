package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Profile ---
    @Query("SELECT * FROM profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<Profile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: Profile)

    // --- Workout Log ---
    @Query("SELECT * FROM workout_log ORDER BY date DESC, id DESC")
    fun getAllWorkouts(): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_log WHERE date = :date ORDER BY id DESC")
    fun getWorkoutsByDate(date: String): Flow<List<WorkoutLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutLog)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutLog)

    @Query("DELETE FROM workout_log WHERE id = :id")
    suspend fun deleteWorkoutById(id: Int)

    // --- Weight Log ---
    @Query("SELECT * FROM weight_log ORDER BY date ASC")
    fun getAllWeights(): Flow<List<WeightLog>>

    @Query("SELECT * FROM weight_log WHERE date = :date LIMIT 1")
    suspend fun getWeightByDate(date: String): WeightLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(weight: WeightLog)

    @Delete
    suspend fun deleteWeight(weight: WeightLog)

    // --- Water Log ---
    @Query("SELECT * FROM water_log ORDER BY date DESC")
    fun getAllWaterLogs(): Flow<List<WaterLog>>

    @Query("SELECT * FROM water_log WHERE date = :date")
    fun getWaterLogsByDate(date: String): Flow<List<WaterLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWater(water: WaterLog)

    @Query("DELETE FROM water_log WHERE date = :date")
    suspend fun clearWaterLogsForDate(date: String)

    // --- Food Log ---
    @Query("SELECT * FROM food_log ORDER BY date DESC")
    fun getAllFoodLogs(): Flow<List<FoodLog>>

    @Query("SELECT * FROM food_log WHERE date = :date")
    fun getFoodLogsByDate(date: String): Flow<List<FoodLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodLog)

    @Delete
    suspend fun deleteFood(food: FoodLog)

    // --- Sleep Log ---
    @Query("SELECT * FROM sleep_log ORDER BY date DESC")
    fun getAllSleepLogs(): Flow<List<SleepLog>>

    @Query("SELECT * FROM sleep_log WHERE date = :date LIMIT 1")
    fun getSleepLogByDate(date: String): Flow<SleepLog?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(sleep: SleepLog)

    @Delete
    suspend fun deleteSleep(sleep: SleepLog)

    // --- Habit Log ---
    @Query("SELECT * FROM habit_log WHERE date = :date LIMIT 1")
    fun getHabitByDate(date: String): Flow<HabitLog?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHabit(habit: HabitLog)

    // --- Progress Photo ---
    @Query("SELECT * FROM progress_photo ORDER BY date DESC")
    fun getAllProgressPhotos(): Flow<List<ProgressPhoto>>

    @Query("SELECT * FROM progress_photo WHERE tag = :tag ORDER BY date DESC")
    fun getProgressPhotosByTag(tag: String): Flow<List<ProgressPhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressPhoto(photo: ProgressPhoto)

    @Delete
    suspend fun deleteProgressPhoto(photo: ProgressPhoto)

    // --- Reminder Settings ---
    @Query("SELECT * FROM reminder_settings WHERE id = 1 LIMIT 1")
    fun getReminderSettings(): Flow<ReminderSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReminders(reminders: ReminderSettings)

    // --- Exercise Library ---
    @Query("SELECT * FROM exercise_library ORDER BY name ASC")
    fun getExerciseLibrary(): Flow<List<ExerciseLibrary>>

    @Query("SELECT * FROM exercise_library WHERE category = :category ORDER BY name ASC")
    fun getExercisesByCategory(category: String): Flow<List<ExerciseLibrary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseLibrary>)

    @Query("SELECT COUNT(*) FROM exercise_library")
    suspend fun getExerciseCount(): Int

    // --- AI Coach Log ---
    @Query("SELECT * FROM ai_coach_log WHERE id = 1 LIMIT 1")
    fun getAiCoachLog(): Flow<AiCoachLog?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAiCoachLog(log: AiCoachLog)
}
