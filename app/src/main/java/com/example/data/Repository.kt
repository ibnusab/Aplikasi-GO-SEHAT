package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class Repository(private val appDao: AppDao) {

    val profile: Flow<Profile?> = appDao.getProfile()
    val allWorkouts: Flow<List<WorkoutLog>> = appDao.getAllWorkouts()
    val allWeights: Flow<List<WeightLog>> = appDao.getAllWeights()
    val allWaterLogs: Flow<List<WaterLog>> = appDao.getAllWaterLogs()
    val allFoodLogs: Flow<List<FoodLog>> = appDao.getAllFoodLogs()
    val allSleepLogs: Flow<List<SleepLog>> = appDao.getAllSleepLogs()
    val allProgressPhotos: Flow<List<ProgressPhoto>> = appDao.getAllProgressPhotos()
    val reminderSettings: Flow<ReminderSettings?> = appDao.getReminderSettings()
    val exerciseLibrary: Flow<List<ExerciseLibrary>> = appDao.getExerciseLibrary()
    val aiCoachLog: Flow<AiCoachLog?> = appDao.getAiCoachLog()

    fun getWorkoutsByDate(date: String): Flow<List<WorkoutLog>> = appDao.getWorkoutsByDate(date)
    fun getWaterLogsByDate(date: String): Flow<List<WaterLog>> = appDao.getWaterLogsByDate(date)
    fun getFoodLogsByDate(date: String): Flow<List<FoodLog>> = appDao.getFoodLogsByDate(date)
    fun getSleepLogByDate(date: String): Flow<SleepLog?> = appDao.getSleepLogByDate(date)
    fun getHabitByDate(date: String): Flow<HabitLog?> = appDao.getHabitByDate(date)
    fun getProgressPhotosByTag(tag: String): Flow<List<ProgressPhoto>> = appDao.getProgressPhotosByTag(tag)
    fun getExercisesByCategory(category: String): Flow<List<ExerciseLibrary>> = appDao.getExercisesByCategory(category)

    suspend fun saveProfile(profile: Profile) = withContext(Dispatchers.IO) {
        appDao.insertOrUpdateProfile(profile)
    }

    suspend fun logWorkout(workout: WorkoutLog) = withContext(Dispatchers.IO) {
        appDao.insertWorkout(workout)
    }

    suspend fun deleteWorkout(workout: WorkoutLog) = withContext(Dispatchers.IO) {
        appDao.deleteWorkout(workout)
    }

    suspend fun deleteWorkoutById(id: Int) = withContext(Dispatchers.IO) {
        appDao.deleteWorkoutById(id)
    }

    suspend fun logWeight(weight: WeightLog) = withContext(Dispatchers.IO) {
        appDao.insertWeight(weight)
    }

    suspend fun deleteWeight(weight: WeightLog) = withContext(Dispatchers.IO) {
        appDao.deleteWeight(weight)
    }

    suspend fun logWater(water: WaterLog) = withContext(Dispatchers.IO) {
        appDao.insertWater(water)
    }

    suspend fun clearWaterLogsForDate(date: String) = withContext(Dispatchers.IO) {
        appDao.clearWaterLogsForDate(date)
    }

    suspend fun logFood(food: FoodLog) = withContext(Dispatchers.IO) {
        appDao.insertFood(food)
    }

    suspend fun deleteFood(food: FoodLog) = withContext(Dispatchers.IO) {
        appDao.deleteFood(food)
    }

    suspend fun logSleep(sleep: SleepLog) = withContext(Dispatchers.IO) {
        appDao.insertSleep(sleep)
    }

    suspend fun deleteSleep(sleep: SleepLog) = withContext(Dispatchers.IO) {
        appDao.deleteSleep(sleep)
    }

    suspend fun saveHabit(habit: HabitLog) = withContext(Dispatchers.IO) {
        appDao.insertOrUpdateHabit(habit)
    }

    suspend fun addProgressPhoto(photo: ProgressPhoto) = withContext(Dispatchers.IO) {
        appDao.insertProgressPhoto(photo)
    }

    suspend fun deleteProgressPhoto(photo: ProgressPhoto) = withContext(Dispatchers.IO) {
        appDao.deleteProgressPhoto(photo)
    }

    suspend fun saveReminders(reminders: ReminderSettings) = withContext(Dispatchers.IO) {
        appDao.insertOrUpdateReminders(reminders)
    }

    suspend fun saveAiCoachLog(log: AiCoachLog) = withContext(Dispatchers.IO) {
        appDao.insertOrUpdateAiCoachLog(log)
    }

    suspend fun seedExercisesIfEmpty() = withContext(Dispatchers.IO) {
        if (appDao.getExerciseCount() > 0) return@withContext

        val list = mutableListOf<ExerciseLibrary>()

        // 1. CHEST (15)
        val chest = listOf(
            "Bench Press" to "Klasik latihan dada dengan flat barbell press.",
            "Incline Dumbbell Press" to "Menargetkan otot dada bagian atas (upper chest).",
            "Decline Bench Press" to "Menargetkan otot dada bagian bawah (lower chest).",
            "Dumbbell Fly" to "Membuka serat dada lateral dan memberikan peregangan maksimal.",
            "Cable Crossover" to "Kontraksi dada optimal menggunakan kabel.",
            "Push Up" to "Latihan berat tubuh dasar namun efektif untuk dada, bahu, dan triceps.",
            "Chest Dips" to "Fokus pada dada bagian bawah dan luar.",
            "Pec Deck Fly" to "Isolasi otot pectoral dengan mesin fly.",
            "Incline Dumbbell Fly" to "Peregangan otot dada atas.",
            "Wide Grip Pushup" to "Push up dengan posisi tangan lebar meningkatkan fokus dada.",
            "Decline Pushup" to "Push up dengan kaki diangkat untuk melatih dada atas.",
            "Floor Press" to "Dumbbell press di lantai untuk melatih kekuatan dada di rentang atas.",
            "Iso-Lateral Chest Press" to "Latihan mesin dada independen untuk keseimbangan otot kiri-kanan.",
            "Dumbbell Pullover" to "Melatih ekspansi rongga dada dan otot pectoral.",
            "Machine Chest Press" to "Presisi dada menggunakan mesin isolasi dada terpandu."
        )
        chest.forEach {
            list.add(ExerciseLibrary(name = it.first, category = "Chest", description = it.second, targetedMuscle = "Pectoralis Major, Anterior Deltoid"))
        }

        // 2. BACK (15)
        val back = listOf(
            "Pull Up" to "Latihan terbaik berat badan untuk punggung lebar.",
            "Chin Up" to "Pull up dengan grip terbalik, melatih punggung tengah dan biceps.",
            "Lat Pulldown" to "Mesin pulldown melatih lats melebar.",
            "Barbell Row" to "Latihan utama untuk punggung tebal dan kuat.",
            "One-Arm Dumbbell Row" to "Isolasi punggung per sisi untuk melatih lats dan rhomboids.",
            "Seated Cable Row" to "Latihan punggung tengah dengan tarikan horizontal.",
            "Deadlift" to "Latihan compound utama melatih seluruh rantai posterior.",
            "T-Bar Row" to "Latihan mendayung sudut miring melatih punggung tengah.",
            "Hyperextension" to "Melatih erector spinae dan punggung bawah secara aman.",
            "Face Pull" to "Melatih bahu belakang dan punggung atas (rhomboids).",
            "Inverted Row" to "Pendayung berat badan menggunakan smith machine.",
            "Barbell Shrugs" to "Latihan utama untuk otot trapezius atas.",
            "Close Grip Lat Pulldown" to "Pulldown sempit berfokus pada ketebalan lats bawah.",
            "Straight-Arm Pulldown" to "Isolasi lats murni tanpa mengaktifkan biceps.",
            "Rack Pulls" to "Deadlift parsial melatih kekuatan punggung atas dan cengkeraman."
        )
        back.forEach {
            list.add(ExerciseLibrary(name = it.first, category = "Back", description = it.second, targetedMuscle = "Latissimus Dorsi, Rhomboids, Trapezius"))
        }

        // 3. SHOULDER (15)
        val shoulder = listOf(
            "Military Press" to "Overhead barbell press legendaris melatih seluruh bahu.",
            "Dumbbell Shoulder Press" to "Tekanan bahu vertikal melatih bahu depan secara seimbang.",
            "Lateral Raise" to "Isolasi bahu samping untuk efek bahu lebar 3D.",
            "Front Raise" to "Isolasi bahu depan dengan dumbbell melatih anterior deltoid.",
            "Rear Delt Fly" to "Latihan isolasi bahu belakang (posterior deltoid).",
            "Arnold Press" to "Tekanan bahu berputar ciptaan Arnold melatih bahu menyeluruh.",
            "Upright Row" to "Tarikan barbell vertikal melatih bahu samping dan trapezius.",
            "Push Press" to "Overhead press eksplosif memanfaatkan momentum kaki.",
            "Cable Lateral Raise" to "Tegangan lateral terus-menerus menggunakan kabel.",
            "Dumbbell Shrugs" to "Melatih otot leher dan trapezius samping dengan dumbbell.",
            "Smith Machine Press" to "Tekanan bahu terpandu meningkatkan stabilitas latihan berat.",
            "Bent-Over Rear Delt Fly" to "Isolasi bahu belakang membungkuk menggunakan dumbbell.",
            "Car Driver" to "Memutar plate di depan dada melatih stabilitas bahu depan.",
            "Pike Pushup" to "Push up posisi pinggul ditekuk ke atas, fokus pada bahu.",
            "Behind the Neck Press" to "Latihan overhead bahu klasik berhati-hati untuk mobilitas bahu."
        )
        shoulder.forEach {
            list.add(ExerciseLibrary(name = it.first, category = "Shoulder", description = it.second, targetedMuscle = "Deltoids (Anterior, Lateral, Posterior)"))
        }

        // 4. LEG (15)
        val leg = listOf(
            "Squat" to "Raja dari segala latihan kaki melatih paha depan, bokong, dan hamstring.",
            "Leg Press" to "Melatih paha depan dengan beban berat secara aman.",
            "Romanian Deadlift" to "Latihan terbaik fokus pada hamstring dan bokong.",
            "Lunge" to "Latihan kaki unilateral meningkatkan keseimbangan dan koordinasi.",
            "Bulgarian Split Squat" to "Split squat intensitas tinggi per kaki melatih kekuatan kuadrisep.",
            "Leg Extension" to "Isolasi paha depan murni menggunakan mesin extension.",
            "Leg Curl" to "Isolasi hamstring murni menggunakan mesin curling.",
            "Calf Raise" to "Latihan utama mengencangkan otot betis.",
            "Goblet Squat" to "Squat dengan memegang dumbbell di depan dada, ramah tulang punggung.",
            "Hack Squat" to "Mesin squat miring melatih paha depan secara dominan.",
            "Step-Up" to "Melangkah ke atas boks melatih kekuatan bokong dan kuadrisep.",
            "Hip Thrust" to "Latihan terbaik mengisolasi dan membesarkan otot glutes (bokong).",
            "Sumo Squat" to "Squat kaki lebar menargetkan paha bagian dalam (adductors).",
            "Box Jump" to "Latihan pliometrik meningkatkan daya ledak otot kaki.",
            "Glute Ham Raise" to "Latihan hamstring berat badan super efektif di bangku khusus."
        )
        leg.forEach {
            list.add(ExerciseLibrary(name = it.first, category = "Leg", description = it.second, targetedMuscle = "Quadriceps, Hamstrings, Gluteus Maximus, Calves"))
        }

        // 5. BICEPS (15)
        val biceps = listOf(
            "Barbell Curl" to "Latihan biceps paling mendasar melatih kekuatan puncak lengan.",
            "Dumbbell Curl" to "Latihan biceps fleksibel per lengan melatih supinasi penuh.",
            "Hammer Curl" to "Melatih biceps bagian luar (brachialis) dan lengan bawah (brachioradialis).",
            "Preacher Curl" to "Isolasi biceps di bangku miring mencegah kecurangan momentum.",
            "Concentration Curl" to "Isolasi puncak biceps tertinggi.",
            "Cable Curl" to "Tegangan konstan sepanjang gerakan bicep curl.",
            "Incline Dumbbell Curl" to "Curl posisi duduk miring memberikan peregangan maksimal.",
            "Spider Curl" to "Curl tengkurap di bangku miring mengisolasi biceps di bagian atas.",
            "Bicep Chin-up" to "Tarikan tubuh berfokus pada kekuatan biceps.",
            "EZ-Bar Curl" to "Curl bar bergerigi mengurangi ketegangan sendi pergelangan tangan.",
            "Zottman Curl" to "Naik secara supinated, turun secara pronated melatih lengan bawah.",
            "Reverse Barbell Curl" to "Curl genggaman terbalik melatih brachioradialis lengan bawah.",
            "Hammer Cable Curl" to "Hammer curl menggunakan tali kabel untuk kontraksi mulus.",
            "Concentration Cable Curl" to "Isolasi bicep kabel tunggal dengan genggaman melayang.",
            "21s Curl" to "Metode curl intensitas tinggi dengan 7 repetisi bawah, 7 atas, 7 penuh."
        )
        biceps.forEach {
            list.add(ExerciseLibrary(name = it.first, category = "Biceps", description = it.second, targetedMuscle = "Biceps Brachii, Brachialis, Brachioradialis"))
        }

        // 6. TRICEPS (15)
        val triceps = listOf(
            "Triceps Pushdown" to "Klasik latihan isolasi triceps menggunakan tali/bar kabel.",
            "Skull Crusher" to "Latihan EZ-Bar mendatar melatih kepala panjang (long head) triceps.",
            "Overhead Extension" to "Peregangan triceps di atas kepala menggunakan dumbbell.",
            "Close-Grip Bench Press" to "Bench press genggaman sempit fokus pada kekuatan triceps.",
            "Tricep Dips" to "Dips bangku sejajar menargetkan triceps dan dada bawah.",
            "Dumbbell Kickback" to "Isolasi kepala lateral triceps di ujung ekstensi.",
            "Diamond Pushup" to "Push up tangan rapat melatih triceps berat tubuh.",
            "Bench Dips" to "Dips bangku datar sangat ramah untuk pemula.",
            "Cable Overhead Extension" to "Overhead extension kabel memberikan ketegangan prima.",
            "Single-Arm Pushdown" to "Pushdown satu tangan mengoreksi ketidakseimbangan otot kiri-kanan.",
            "Rope Pushdown" to "Pushdown tali melebar ke luar untuk kontraksi maksimal triceps.",
            "EZ-Bar Skullcrusher" to "Skullcrusher menggunakan EZ-Bar melonggarkan pergelangan.",
            "Dumbbell Kickback Cable" to "Isolasi kickback murni menggunakan kabel bawah.",
            "Floor Press Close Grip" to "Presisi triceps di lantai melatih penguncian sendi.",
            "Machine Triceps Extension" to "Isolasi triceps stabil menggunakan mesin ekstensi duduk."
        )
        triceps.forEach {
            list.add(ExerciseLibrary(name = it.first, category = "Triceps", description = it.second, targetedMuscle = "Triceps Brachii (Long, Lateral, Medial Heads)"))
        }

        // 7. CORE (15)
        val core = listOf(
            "Plank" to "Latihan isometrik terbaik memperkuat stabilitas core tubuh.",
            "Sit Up" to "Latihan dinamis melatih rectus abdominis bagian atas.",
            "Russian Twist" to "Gerakan rotasi memperkuat otot obliques (pinggir perut).",
            "Hanging Leg Raise" to "Latihan perut bawah intensitas tinggi melatih kekuatan core bawah.",
            "Ab Wheel Rollout" to "Gerakan menantang melatih stabilitas core keseluruhan.",
            "Bicycle Crunch" to "Mengaktifkan abdominis dan obliques secara simultan.",
            "Reverse Crunch" to "Mengangkat pinggul fokus pada otot perut bawah.",
            "Mountain Climber" to "Latihan dinamis melatih core serta meningkatkan denyut jantung.",
            "Dead Bug" to "Gerakan koordinasi core melatih stabilitas tulang belakang.",
            "Cable Woodchopper" to "Latihan rotasional core dengan beban kabel.",
            "Side Plank" to "Stabilitas isometrik lateral melatih obliques.",
            "Toes to Bar" to "Mengangkat kaki menyentuh palang melatih core tingkat lanjut.",
            "Dragon Flag" to "Gerakan core legendaris ciptaan Bruce Lee melatih core total.",
            "Bird Dog" to "Latihan stabilitas punggung dan perut berfokus pada postur.",
            "Flutter Kicks" to "Gerakan kayuhan kaki di lantai menargetkan perut bagian bawah."
        )
        core.forEach {
            list.add(ExerciseLibrary(name = it.first, category = "Core", description = it.second, targetedMuscle = "Rectus Abdominis, Obliques, Transversus Abdominis"))
        }

        appDao.insertExercises(list)
    }
}
