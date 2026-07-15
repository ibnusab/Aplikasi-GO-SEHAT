package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log10

class MainViewModel(private val repository: Repository) : ViewModel() {

    // --- Authentication ---
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // --- Date handling ---
    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // --- Data Flows ---
    val profile: StateFlow<Profile> = repository.profile
        .map { it ?: Profile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Profile())

    val allWorkouts: StateFlow<List<WorkoutLog>> = repository.allWorkouts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWeights: StateFlow<List<WeightLog>> = repository.allWeights
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProgressPhotos: StateFlow<List<ProgressPhoto>> = repository.allProgressPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminderSettings: StateFlow<ReminderSettings> = repository.reminderSettings
        .map { it ?: ReminderSettings() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReminderSettings())

    val exerciseLibrary: StateFlow<List<ExerciseLibrary>> = repository.exerciseLibrary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Today's Metrics (Reactive) ---
    private val today = getTodayDate()

    val workoutsToday: StateFlow<List<WorkoutLog>> = repository.getWorkoutsByDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val waterLogsToday: StateFlow<List<WaterLog>> = repository.getWaterLogsByDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val foodLogsToday: StateFlow<List<FoodLog>> = repository.getFoodLogsByDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sleepToday: StateFlow<SleepLog?> = repository.getSleepLogByDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val habitToday: StateFlow<HabitLog> = repository.getHabitByDate(today)
        .map { it ?: HabitLog(date = today) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HabitLog(date = today))

    // --- Active category for exercise search ---
    private val _exerciseFilterCategory = MutableStateFlow("Chest")
    val exerciseFilterCategory: StateFlow<String> = _exerciseFilterCategory.asStateFlow()

    // --- AI Premium States ---
    private val _aiRecommendation = MutableStateFlow<String>("")
    val aiRecommendation: StateFlow<String> = _aiRecommendation.asStateFlow()

    private val _aiRecommendationLoading = MutableStateFlow(false)
    val aiRecommendationLoading: StateFlow<Boolean> = _aiRecommendationLoading.asStateFlow()

    private val _aiFoodRecognition = MutableStateFlow<String>("")
    val aiFoodRecognition: StateFlow<String> = _aiFoodRecognition.asStateFlow()

    private val _aiFoodRecognitionLoading = MutableStateFlow(false)
    val aiFoodRecognitionLoading: StateFlow<Boolean> = _aiFoodRecognitionLoading.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedExercisesIfEmpty()
        }
        viewModelScope.launch {
            repository.aiCoachLog.collect { log ->
                if (log != null) {
                    _aiRecommendation.value = log.workoutRecommendation
                    _aiFoodRecognition.value = log.foodRecognitionResult
                }
            }
        }
    }

    // --- Auth Actions ---
    fun login(email: String, password: String, isFingerprint: Boolean = false) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Simple simulated auth with standard feedback
            if (isFingerprint) {
                _authState.value = AuthState.Authenticated(email = "user.biometric@gosehat.com", name = "User Sehat")
            } else if (email.contains("@") && password.length >= 6) {
                val displayName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                _authState.value = AuthState.Authenticated(email = email, name = displayName)
            } else {
                _authState.value = AuthState.Error("Email tidak valid atau password kurang dari 6 karakter.")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            if (name.isNotEmpty() && email.contains("@") && password.length >= 6) {
                _authState.value = AuthState.Authenticated(email = email, name = name)
                // Initialize default profile with the registered name
                repository.saveProfile(Profile(name = name))
            } else {
                _authState.value = AuthState.Error("Mohon lengkapi data dengan benar. Password minimal 6 karakter.")
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.ForgotPasswordSent(email)
        }
    }

    fun logout() {
        _authState.value = AuthState.Unauthenticated
    }

    fun resetAuthState() {
        _authState.value = AuthState.Unauthenticated
    }

    // --- Profile Settings ---
    fun updateProfile(
        name: String,
        age: Int,
        gender: String,
        height: Float,
        weight: Float,
        targetWeight: Float,
        activityLevel: String,
        fitnessGoal: String
    ) {
        viewModelScope.launch {
            val updated = Profile(
                name = name,
                age = age,
                gender = gender,
                height = height,
                weight = weight,
                targetWeight = targetWeight,
                activityLevel = activityLevel,
                fitnessGoal = fitnessGoal
            )
            repository.saveProfile(updated)
            // Log today's weight automatically to the weight logger
            repository.logWeight(WeightLog(date = getTodayDate(), weightKg = weight))
        }
    }

    // --- Weight Logging ---
    fun logWeight(weight: Float, date: String = getTodayDate()) {
        viewModelScope.launch {
            repository.logWeight(WeightLog(date = date, weightKg = weight))
            // Also update the current profile weight
            val currentProfile = profile.value
            repository.saveProfile(currentProfile.copy(weight = weight))
        }
    }

    // --- Workout Log Actions ---
    fun addWorkoutLog(exerciseName: String, weight: Float, sets: Int, reps: Int, duration: Int, calories: Int, date: String = getTodayDate()) {
        viewModelScope.launch {
            repository.logWorkout(
                WorkoutLog(
                    date = date,
                    exerciseName = exerciseName,
                    weightKg = weight,
                    sets = sets,
                    reps = reps,
                    durationMinutes = duration,
                    caloriesBurned = calories
                )
            )
            // Auto check workout habit
            val habit = habitToday.value
            repository.saveHabit(habit.copy(workoutChecked = true))
        }
    }

    fun deleteWorkout(workout: WorkoutLog) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
        }
    }

    // --- Water Log Actions ---
    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            val todayDate = getTodayDate()
            repository.logWater(WaterLog(date = todayDate, amountMl = amountMl))
            // Auto check water habit if target likely reached (e.g., total >= 2000ml)
            val currentWaterTotal = waterLogsToday.value.sumOf { it.amountMl } + amountMl
            if (currentWaterTotal >= 2000) {
                val habit = habitToday.value
                repository.saveHabit(habit.copy(waterChecked = true))
            }
        }
    }

    fun resetWater() {
        viewModelScope.launch {
            repository.clearWaterLogsForDate(getTodayDate())
            val habit = habitToday.value
            repository.saveHabit(habit.copy(waterChecked = false))
        }
    }

    // --- Nutrition Log Actions ---
    fun addFoodLog(mealType: String, foodName: String, calories: Int, protein: Float, carbs: Float, fats: Float) {
        viewModelScope.launch {
            repository.logFood(
                FoodLog(
                    date = getTodayDate(),
                    mealType = mealType,
                    foodName = foodName,
                    calories = calories,
                    proteinGrams = protein,
                    carbsGrams = carbs,
                    fatsGrams = fats
                )
            )
            // Auto check protein habit if daily protein total meets some threshold
            val currentProtein = foodLogsToday.value.sumOf { it.proteinGrams.toDouble() } + protein
            val targetProtein = calculateProteinRequirement()
            if (currentProtein >= targetProtein) {
                val habit = habitToday.value
                repository.saveHabit(habit.copy(proteinChecked = true))
            }
        }
    }

    fun deleteFood(food: FoodLog) {
        viewModelScope.launch {
            repository.deleteFood(food)
        }
    }

    // --- Sleep Tracker ---
    fun logSleep(sleepTime: String, wakeTime: String, durationHours: Float, qualityScore: Int) {
        viewModelScope.launch {
            repository.logSleep(
                SleepLog(
                    date = getTodayDate(),
                    sleepTime = sleepTime,
                    wakeTime = wakeTime,
                    durationHours = durationHours,
                    qualityScore = qualityScore
                )
            )
            // Auto check sleep habit if duration is good (e.g., >= 7 hours)
            if (durationHours >= 7) {
                val habit = habitToday.value
                repository.saveHabit(habit.copy(sleepChecked = true))
            }
        }
    }

    // --- Habit Tracker ---
    fun toggleHabit(type: String) {
        viewModelScope.launch {
            val current = habitToday.value
            val updated = when (type) {
                "workout" -> current.copy(workoutChecked = !current.workoutChecked)
                "water" -> current.copy(waterChecked = !current.waterChecked)
                "protein" -> current.copy(proteinChecked = !current.proteinChecked)
                "sleep" -> current.copy(sleepChecked = !current.sleepChecked)
                "stretching" -> current.copy(stretchingChecked = !current.stretchingChecked)
                "cardio" -> current.copy(cardioChecked = !current.cardioChecked)
                else -> current
            }
            repository.saveHabit(updated)
        }
    }

    // --- Progress Photo ---
    fun addProgressPhoto(uriString: String, tag: String) {
        viewModelScope.launch {
            repository.addProgressPhoto(
                ProgressPhoto(
                    date = getTodayDate(),
                    imageUri = uriString,
                    tag = tag
                )
            )
        }
    }

    fun deleteProgressPhoto(photo: ProgressPhoto) {
        viewModelScope.launch {
            repository.deleteProgressPhoto(photo)
        }
    }

    // --- Exercise Filter Category ---
    fun setExerciseFilterCategory(category: String) {
        _exerciseFilterCategory.value = category
    }

    // --- Reminder Settings ---
    fun updateReminderSettings(
        workoutEnabled: Boolean,
        workoutTime: String,
        waterEnabled: Boolean,
        waterInterval: Int,
        mealEnabled: Boolean,
        mealTimes: String,
        sleepEnabled: Boolean,
        sleepTime: String,
        weightEnabled: Boolean,
        weightTime: String
    ) {
        viewModelScope.launch {
            val reminders = ReminderSettings(
                id = 1,
                workoutEnabled = workoutEnabled,
                workoutTime = workoutTime,
                waterEnabled = waterEnabled,
                waterIntervalHours = waterInterval,
                mealEnabled = mealEnabled,
                mealTimes = mealTimes,
                sleepEnabled = sleepEnabled,
                sleepTime = sleepTime,
                weightEnabled = weightEnabled,
                weightTime = weightTime
            )
            repository.saveReminders(reminders)
        }
    }

    // --- Fitness Calculators & Math ---

    fun calculateBmi(weight: Float = profile.value.weight, height: Float = profile.value.height): Float {
        if (height <= 0) return 0f
        val heightMeters = height / 100f
        return weight / (heightMeters * heightMeters)
    }

    fun getBmiCategory(bmi: Float): String {
        return when {
            bmi < 18.5f -> "Kurus (Underweight)"
            bmi < 25.0f -> "Normal"
            bmi < 30.0f -> "Overweight"
            else -> "Obesitas (Obese)"
        }
    }

    /**
     * Body Fat Percentage using US Navy Method
     * Inputs: height, weight, waist, neck, gender, hip (optional, for female)
     */
    fun calculateBodyFat(
        heightCm: Float,
        waistCm: Float,
        neckCm: Float,
        gender: String,
        hipCm: Float = 0f
    ): Float {
        if (heightCm <= 0f || waistCm <= neckCm || neckCm <= 0f) return 0f
        return try {
            if (gender.lowercase() == "male") {
                // Male formula: 86.010 * log10(waist_in - neck_in) - 70.041 * log10(height_in) + 36.76
                val waistIn = waistCm / 2.54
                val neckIn = neckCm / 2.54
                val heightIn = heightCm / 2.54
                val diff = waistIn - neckIn
                if (diff <= 0) return 0f
                val bf = 86.010 * log10(diff) - 70.041 * log10(heightIn) + 36.76
                bf.toFloat().coerceIn(2f, 60f)
            } else {
                // Female formula: 163.205 * log10(waist_in + hip_in - neck_in) - 97.684 * log10(height_in) - 78.387
                val waistIn = waistCm / 2.54
                val neckIn = neckCm / 2.54
                val heightIn = heightCm / 2.54
                val hipIn = if (hipCm > 0f) hipCm / 2.54 else (waistCm + 10f) / 2.54 // approximation if hip is empty
                val diff = waistIn + hipIn - neckIn
                if (diff <= 0) return 0f
                val bf = 163.205 * log10(diff) - 97.684 * log10(heightIn) - 78.387
                bf.toFloat().coerceIn(2f, 60f)
            }
        } catch (e: Exception) {
            0f
        }
    }

    fun getBodyFatCategory(bf: Float, gender: String): String {
        return if (gender.lowercase() == "male") {
            when {
                bf < 6f -> "Essential Fat"
                bf < 14f -> "Athletes"
                bf < 18f -> "Fitness"
                bf < 25f -> "Acceptable"
                else -> "Obese"
            }
        } else {
            when {
                bf < 14f -> "Essential Fat"
                bf < 21f -> "Athletes"
                bf < 25f -> "Fitness"
                bf < 32f -> "Acceptable"
                else -> "Obese"
            }
        }
    }

    /**
     * Daily Calorie Calculations using Mifflin-St Jeor
     */
    fun calculateBmr(p: Profile = profile.value): Float {
        return if (p.gender.lowercase() == "male") {
            10f * p.weight + 6.25f * p.height - 5f * p.age + 5f
        } else {
            10f * p.weight + 6.25f * p.height - 5f * p.age - 161f
        }
    }

    fun calculateTdee(bmr: Float = calculateBmr(), activity: String = profile.value.activityLevel): Float {
        val factor = when (activity.lowercase()) {
            "sedentary" -> 1.2f
            "light" -> 1.375f
            "moderate" -> 1.55f
            "active" -> 1.725f
            "very active" -> 1.9f
            else -> 1.55f
        }
        return bmr * factor
    }

    fun calculateProteinRequirement(p: Profile = profile.value): Float {
        val multiplier = when (p.fitnessGoal.lowercase()) {
            "build muscle", "bulking" -> 2.2f
            "lose weight", "cutting" -> 1.8f
            "calisthenics" -> 2.0f
            else -> 1.6f
        }
        return p.weight * multiplier
    }

    // --- Premium AI Coach Features powered by Gemini 3.5 Flash REST API ---

    fun getAiWorkoutRecommendations() {
        viewModelScope.launch {
            _aiRecommendationLoading.value = true
            val apiKey = RetrofitClient.getApiKey()
            val p = profile.value

            val prompt = """
                Bertindak sebagai Personal Trainer GO SEHAT AI bersertifikasi Internasional. 
                Berikan rekomendasi program latihan gym dan kebugaran terstruktur khusus berdasarkan profil berikut:
                - Nama: ${p.name}
                - Umur: ${p.age} tahun
                - Jenis Kelamin: ${p.gender}
                - Tinggi: ${p.height} cm
                - Berat: ${p.weight} kg (Target: ${p.targetWeight} kg)
                - Tingkat Aktivitas: ${p.activityLevel}
                - Tujuan Fitness: ${p.fitnessGoal}

                Format keluaran harus menarik, terstruktur, mencantumkan:
                1. Analisis Singkat Profil & Prediksi Waktu Mencapai Target (berdasarkan defisit/surplus realistis).
                2. Program Latihan Mingguan Detail (Misal: Senin-Chest, Rabu-Back, dll, mencakup nama latihan, set, reps, dan waktu istirahat).
                3. Tips GO SEHAT Nutrisi & Pemulihan khusus.
                Gunakan bahasa Indonesia yang profesional, bersemangat, dan memotivasi ala GO SEHAT!
            """.trimIndent()

            if (apiKey.isEmpty()) {
                // Fallback to simulated high-quality generator if API Key is not configured
                val fallback = getSimulatedAiWorkoutRecommendation(p)
                _aiRecommendation.value = fallback
                repository.saveAiCoachLog(AiCoachLog(id = 1, workoutRecommendation = fallback, foodRecognitionResult = _aiFoodRecognition.value))
                _aiRecommendationLoading.value = false
                return@launch
            }

            try {
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )
                val response = RetrofitClient.service.generateContent(apiKey, request)
                val textResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                val result = textResponse ?: "Gagal memproses rekomendasi AI. Mohon coba lagi."
                _aiRecommendation.value = result
                repository.saveAiCoachLog(AiCoachLog(id = 1, workoutRecommendation = result, foodRecognitionResult = _aiFoodRecognition.value))
            } catch (e: Exception) {
                Log.e("GoSehatAI", "Gemini API error: ", e)
                val errResult = "Koneksi AI gagal. Berikut adalah rekomendasi lokal untuk Anda:\n\n" + getSimulatedAiWorkoutRecommendation(p)
                _aiRecommendation.value = errResult
                repository.saveAiCoachLog(AiCoachLog(id = 1, workoutRecommendation = errResult, foodRecognitionResult = _aiFoodRecognition.value))
            } finally {
                _aiRecommendationLoading.value = false
            }
        }
    }

    fun analyzeFoodImage(foodDescription: String, bitmap: Bitmap?) {
        viewModelScope.launch {
            _aiFoodRecognitionLoading.value = true
            val apiKey = RetrofitClient.getApiKey()

            val prompt = """
                Bertindak sebagai Ahli Gizi / Nutrition Coach AI GO SEHAT.
                Analisis makanan berikut: "$foodDescription".
                Estimasi kandungan gizinya secara detail:
                - Nama Makanan (Kontekstualisasi budaya kuliner Indonesia)
                - Kalori (kkal)
                - Protein (g)
                - Karbohidrat (g)
                - Lemak (g)
                Format keluaran:
                Berikan tabel ringkasan nutrisi yang jelas, diikuti dengan analisis singkat apakah makanan ini cocok untuk target pengguna (${profile.value.fitnessGoal}) beserta saran pengganti yang lebih sehat (Alternatif Sehat).
                Gunakan Bahasa Indonesia yang ramah, informatif, dan praktis!
            """.trimIndent()

            if (apiKey.isEmpty()) {
                val fallback = getSimulatedFoodAnalysis(foodDescription)
                _aiFoodRecognition.value = fallback
                repository.saveAiCoachLog(AiCoachLog(id = 1, workoutRecommendation = _aiRecommendation.value, foodRecognitionResult = fallback))
                _aiFoodRecognitionLoading.value = false
                return@launch
            }

            try {
                val parts = mutableListOf<Part>()
                parts.add(Part(text = prompt))
                if (bitmap != null) {
                    val base64Image = bitmap.toBase64()
                    parts.add(Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image)))
                }

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = parts))
                )
                val response = RetrofitClient.service.generateContent(apiKey, request)
                val textResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                val result = textResponse ?: "Gagal menganalisis gizi makanan. Mohon coba lagi."
                _aiFoodRecognition.value = result
                repository.saveAiCoachLog(AiCoachLog(id = 1, workoutRecommendation = _aiRecommendation.value, foodRecognitionResult = result))
            } catch (e: Exception) {
                Log.e("GoSehatAI", "Gemini Food API error: ", e)
                val errResult = "Koneksi AI gagal. Berikut adalah estimasi gizi lokal untuk makanan Anda:\n\n" + getSimulatedFoodAnalysis(foodDescription)
                _aiFoodRecognition.value = errResult
                repository.saveAiCoachLog(AiCoachLog(id = 1, workoutRecommendation = _aiRecommendation.value, foodRecognitionResult = errResult))
            } finally {
                _aiFoodRecognitionLoading.value = false
            }
        }
    }

    // Helper to convert Bitmap to Base64
    private fun Bitmap.toBase64(): String {
        val outputStream = java.io.ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return android.util.Base64.encodeToString(outputStream.toByteArray(), android.util.Base64.NO_WRAP)
    }

    // --- High-Quality Mock Fallbacks (Preventing API Failures & Outages) ---

    private fun getSimulatedAiWorkoutRecommendation(p: Profile): String {
        val bmr = calculateBmr(p)
        val tdee = calculateTdee(bmr, p.activityLevel)
        val targetWeeks = try {
            val weightDiff = Math.abs(p.weight - p.targetWeight)
            if (weightDiff == 0f) 1 else Math.ceil((weightDiff / 0.5)).toInt()
        } catch (e: Exception) {
            8
        }

        return """
            🔥 **REKOMENDASI PERSONAL TRAINING GO SEHAT** 🔥
            
            Halo **${p.name}**, mari latih tubuh cerdas untuk mencapai tujuanmu: **${p.fitnessGoal}**!
            
            ### 📊 **ANALISIS FISIK & PREDIKSI TARGET**
            * **BMI Anda**: ${String.format("%.1f", calculateBmi(p.weight, p.height))} (${getBmiCategory(calculateBmi(p.weight, p.height))})
            * **BMR**: ${bmr.toInt()} kkal/hari
            * **Kebutuhan Kalori Harian (TDEE)**: ${tdee.toInt()} kkal/hari
            * **Prediksi Waktu Target**: Sekitar **$targetWeeks Minggu** untuk berpindah dari **${p.weight} kg** ke **${p.targetWeight} kg** dengan perubahan berat badan sehat 0.5 kg/minggu.
            
            ---
            
            ### 🏋️ **PROGRAM LATIHAN MINGGUAN (Rekomendasi Sehat)**
            
            Berdasarkan tujuan **${p.fitnessGoal}**, program ideal untuk Anda adalah kombinasi **Strength Hypertrophy**:
            
            *   **Senin: PUSH DAY (Dada, Bahu, Triceps)**
                *   *Bench Press*: 4 Set x 8-12 Reps (Istirahat 90 detik)
                *   *Incline Dumbbell Press*: 3 Set x 10 Reps
                *   *Lateral Raise (Bahu Samping)*: 4 Set x 15 Reps
                *   *Triceps Pushdown*: 3 Set x 12 Reps
                
            *   **Rabu: PULL DAY (Punggung, Biceps, Core)**
                *   *Lat Pulldown / Pull Up*: 4 Set x 8-12 Reps
                *   *Barbell Row (Punggung Tengah)*: 3 Set x 10 Reps
                *   *Dumbbell Curl*: 4 Set x 12 Reps
                *   *Plank*: 3 Set x 60 detik
                
            *   **Jumat: LEGS DAY (Paha Depan, Hamstring, Betis)**
                *   *Squat (Barbell/Goblet)*: 4 Set x 8-10 Reps (Istirahat 2 menit)
                *   *Romanian Deadlift*: 3 Set x 10 Reps
                *   *Leg Curl*: 3 Set x 12 Reps
                *   *Calf Raises*: 4 Set x 15 Reps
                
            *   **Sabtu: CORE & CARDIO ACTIVE RECOVERY**
                *   *Bicycle Crunches*: 3 Set x 20 Reps
                *   *Jogging / HIIT*: 20-30 Menit (Intensitas Sedang)
            
            ---
            
            ### 🍏 **REKOMENDASI GO SEHAT NUTRISI**
            *   **Kebutuhan Protein**: **${calculateProteinRequirement(p).toInt()} gram/hari** (Sangat vital untuk pertumbuhan otot dan pemulihan sel).
            *   **Target Kalori**: 
                *   Jika bulking/gain muscle: **${(tdee + 400).toInt()} kkal/hari** (surplus ringan).
                *   Jika cutting/fat loss: **${(tdee - 400).toInt()} kkal/hari** (defisit sehat).
            *   **Air Minum**: Minimal 3 Liter per hari untuk mencegah dehidrasi otot.
        """.trimIndent()
    }

    private fun getSimulatedFoodAnalysis(foodDescription: String): String {
        val lowercaseFood = foodDescription.lowercase()
        val foodName = foodDescription.replaceFirstChar { it.uppercase() }
        
        val (calories, protein, carbs, fats) = when {
            lowercaseFood.contains("nasi goreng") -> qzFood(380, 10f, 55f, 14f)
            lowercaseFood.contains("ayam bakar") || lowercaseFood.contains("ayam goreng") -> qzFood(260, 25f, 2f, 15f)
            lowercaseFood.contains("telur") || lowercaseFood.contains("egg") -> qzFood(150, 13f, 1f, 10f)
            lowercaseFood.contains("tempe") || lowercaseFood.contains("tahu") -> qzFood(120, 8f, 10f, 6f)
            lowercaseFood.contains("pisang") || lowercaseFood.contains("banana") -> qzFood(90, 1.2f, 23f, 0.3f)
            lowercaseFood.contains("oatmeal") -> qzFood(150, 5f, 27f, 2.5f)
            lowercaseFood.contains("nasi putih") -> qzFood(130, 2.4f, 28f, 0.2f)
            else -> qzFood(210, 12f, 22f, 8f) // default average food
        }

        val suitable = if (profile.value.fitnessGoal.lowercase().contains("lose") && calories > 300) {
            "Sangat disarankan untuk membatasi porsi atau memasak dengan sedikit minyak agar mendukung program defisit kalori Anda."
        } else {
            "Sangat cocok untuk menyuplai energi latihan dan memicu pemulihan otot protein harian Anda!"
        }

        return """
            📷 **HASIL ANALISIS GIZI MAKANAN GO SEHAT** 🍏
            
            Anda menginput makanan: **$foodName**
            
            ### 📊 **ESTIMASI NUTRISI (Per 1 Porsi Standar)**
            
            | Nutrisi | Jumlah Estimasi | Target Harian Anda |
            | :--- | :--- | :--- |
            | **🔥 Energi (Kalori)** | **$calories kkal** | ${calculateTdee().toInt()} kkal |
            | **💪 Protein** | **${protein} g** | ${calculateProteinRequirement().toInt()} g |
            | **🍞 Karbohidrat** | **${carbs} g** | ~250 g |
            | **🥑 Lemak** | **${fats} g** | ~60 g |
            
            ---
            
            ### 💡 **ANALISIS DIET SEHAT**
            *   **Kecocokan Goal (${profile.value.fitnessGoal})**: $suitable
            *   **Rekomendasi Cara Memasak**: Kukus, bakar, atau tumis dengan sedikit minyak zaitun (olive oil) daripada digoreng deep-fry.
            *   **Alternatif Sehat**: Jika bosan, Anda bisa menggantinya dengan Dada Ayam Panggang + Brokoli Kukus yang memiliki densitas nutrisi luar biasa tinggi dengan kalori rendah!
        """.trimIndent()
    }

    private data class qzFood(val c: Int, val p: Float, val carb: Float, val f: Float)
}

// Factory for MainViewModel
class MainViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// Sealed AuthState class to handle authentications cleanly
sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val email: String, val name: String) : AuthState()
    data class ForgotPasswordSent(val email: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
