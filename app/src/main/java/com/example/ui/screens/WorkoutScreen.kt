package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExerciseLibrary
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import kotlin.math.sin

@Composable
fun WorkoutScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val exerciseLibrary by viewModel.exerciseLibrary.collectAsState()
    val filterCategory by viewModel.exerciseFilterCategory.collectAsState()
    
    var selectedProgramCategory by remember { mutableStateOf("Push") }
    var searchQuery by remember { mutableStateOf("") }
    
    // Logging Dialog States
    var selectedExerciseForLogging by remember { mutableStateOf<ExerciseLibrary?>(null) }
    var inputWeight by remember { mutableStateOf("20") }
    var inputSets by remember { mutableStateOf("4") }
    var inputReps by remember { mutableStateOf("12") }
    var inputDuration by remember { mutableStateOf("45") }
    var inputCalories by remember { mutableStateOf("150") }

    val programCategories = listOf(
        "Push", "Pull", "Legs", "Full Body", "Upper Lower", "PPL", "Calisthenics", "Home Workout"
    )

    val exerciseFilterTabs = listOf(
        "Chest", "Back", "Shoulder", "Leg", "Biceps", "Triceps", "Core"
    )

    // Current Program Description generator
    val activeProgramDesc = when (selectedProgramCategory) {
        "Push" -> "Fokus pada dada (chest), bahu depan/samping, dan lengan belakang (triceps)."
        "Pull" -> "Fokus pada punggung (back), bahu belakang, biceps, dan lengan depan."
        "Legs" -> "Fokus melatih kekuatan paha depan (quadriceps), hamstring, bokong (glutes), dan betis."
        "Full Body" -> "Latihan compound menyeluruh untuk meningkatkan stamina dan kekuatan seluruh tubuh."
        "Upper Lower" -> "Pembagian latihan atas tubuh dan bawah tubuh untuk recovery otot seimbang."
        "PPL" -> "Program atletis tiga hari Push-Pull-Legs legendaris untuk hipertrofi otot murni."
        "Calisthenics" -> "Fokus pada latihan berat badan murni (bodyweight) untuk kelenturan dan kekuatan fungsional."
        "Home Workout" -> "Latihan tanpa alat beban berat, ramah untuk dilakukan di kamar atau ruang tamu."
        else -> "Program latihan Sigma."
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Program Selector Title
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PROGRAM LATIHAN SIGMA",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Text(
                text = "Train Smarter. Pilih program latihan ideal Anda hari ini.",
                fontSize = 12.sp,
                color = TextGray
            )
        }

        // Horizontal list of programs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(programCategories) { category ->
                    val isSelected = selectedProgramCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedProgramCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SigmaOrange,
                            selectedLabelColor = Color.White,
                            containerColor = CarbonCard,
                            labelColor = TextGray
                        ),
                        modifier = Modifier.testTag("program_chip_$category")
                    )
                }
            }
        }

        // Program Details Card with Procedural Figure Animation Canvas
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonCard),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "KATEGORI: $selectedProgramCategory",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SigmaOrange
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activeProgramDesc,
                                fontSize = 12.sp,
                                color = OnCarbonDark
                            )
                        }

                        // Animated Figure
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(CarbonCardElevated, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            FitnessFigureAnimator(workoutType = selectedProgramCategory)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text("DURASI", fontSize = 10.sp, color = TextGray)
                            Text("45-60 mnt", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("INTENSITAS", fontSize = 10.sp, color = TextGray)
                            Text("Sedang-Tinggi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SigmaGreen)
                        }
                        Column {
                            Text("REKOMENDASI SET", fontSize = 10.sp, color = TextGray)
                            Text("3-4 Set / Latihan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Exercise Library Title & Search
        item {
            Text(
                text = "EXERCISE LIBRARY",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Text(
                text = "Browse lebih dari 100 latihan terstruktur di GO SEHAT.",
                fontSize = 12.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari gerakan latihan...", color = TextGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SigmaOrange) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SigmaOrange,
                    unfocusedBorderColor = CarbonCardElevated,
                    unfocusedContainerColor = CarbonCard,
                    focusedContainerColor = CarbonCard
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exercise_search_bar")
            )
        }

        // Exercise Library Target Categories Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(exerciseFilterTabs) { category ->
                    val isSelected = filterCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setExerciseFilterCategory(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SigmaGreen,
                            selectedLabelColor = Color.Black,
                            containerColor = CarbonCard,
                            labelColor = TextGray
                        ),
                        modifier = Modifier.testTag("exercise_tab_$category")
                    )
                }
            }
        }

        // List of Seeded Exercises
        val filteredExercises = exerciseLibrary.filter {
            it.category == filterCategory &&
            (searchQuery.isEmpty() || it.name.lowercase().contains(searchQuery.lowercase()))
        }

        if (filteredExercises.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextGray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Latihan tidak ditemukan.", color = TextGray, fontSize = 14.sp)
                }
            }
        } else {
            items(filteredExercises) { exercise ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CarbonCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exercise_item_${exercise.name.replace(" ", "_")}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = exercise.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Otot dilatih: ${exercise.targetedMuscle}",
                                    fontSize = 11.sp,
                                    color = SigmaGreen
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(CarbonCardElevated, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${exercise.defaultSets} Set x ${exercise.defaultReps}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = exercise.description,
                            fontSize = 12.sp,
                            color = OnCarbonDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                IconLabel(icon = Icons.Default.Timer, label = exercise.defaultDuration)
                                IconLabel(icon = Icons.Default.LocalFireDepartment, label = "~${exercise.caloriesBurnedPerSet * exercise.defaultSets} kkal", tint = SigmaOrange)
                            }

                            Button(
                                onClick = {
                                    selectedExerciseForLogging = exercise
                                    inputWeight = "20"
                                    inputSets = exercise.defaultSets.toString()
                                    inputReps = "12"
                                    inputDuration = "45"
                                    inputCalories = (exercise.caloriesBurnedPerSet * exercise.defaultSets).toString()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SigmaOrange),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Catat Latihan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp)) // Avoid blocking bottom navigation
        }
    }

    // Modal Dialog to log Workout
    if (selectedExerciseForLogging != null) {
        AlertDialog(
            onDismissRequest = { selectedExerciseForLogging = null },
            title = { Text("Log Workout: ${selectedExerciseForLogging?.name}") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Lengkapi detail latihan untuk mencatat progres Anda harian.", fontSize = 12.sp, color = TextGray)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = inputWeight,
                            onValueChange = { inputWeight = it },
                            label = { Text("Beban (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )
                        OutlinedTextField(
                            value = inputSets,
                            onValueChange = { inputSets = it },
                            label = { Text("Sets") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = inputReps,
                            onValueChange = { inputReps = it },
                            label = { Text("Reps") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )
                        OutlinedTextField(
                            value = inputDuration,
                            onValueChange = { inputDuration = it },
                            label = { Text("Durasi (mnt)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )
                    }

                    OutlinedTextField(
                        value = inputCalories,
                        onValueChange = { inputCalories = it },
                        label = { Text("Kalori Terbakar (kkal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val exercise = selectedExerciseForLogging
                        if (exercise != null) {
                            val weight = inputWeight.toFloatOrNull() ?: 20f
                            val sets = inputSets.toIntOrNull() ?: 4
                            val reps = inputReps.toIntOrNull() ?: 12
                            val duration = inputDuration.toIntOrNull() ?: 45
                            val calories = inputCalories.toIntOrNull() ?: 150
                            viewModel.addWorkoutLog(
                                exerciseName = exercise.name,
                                weight = weight,
                                sets = sets,
                                reps = reps,
                                duration = duration,
                                calories = calories
                            )
                        }
                        selectedExerciseForLogging = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SigmaGreen)
                ) {
                    Text("Simpan Log", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedExerciseForLogging = null }) {
                    Text("Batal", color = Color.White)
                }
            },
            containerColor = CarbonCard,
            titleContentColor = Color.White,
            textContentColor = OnCarbonDark
        )
    }
}

@Composable
fun IconLabel(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color = TextGray
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = tint)
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 11.sp, color = TextGray)
    }
}

// Custom Procedural Animator representing Fitness Motions on Canvas
@Composable
fun FitnessFigureAnimator(
    workoutType: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lifting_loop")
    val animationState by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lift"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val centerY = height / 2

        when {
            // Push Day (Bench Press / Pushup motions)
            workoutType == "Push" || workoutType == "PPL" -> {
                // Barbell Motion
                val barY = centerY - 15f + (animationState * 25f)
                // Draw Barbell
                drawLine(
                    color = SigmaOrange,
                    start = Offset(centerX - 25f, barY),
                    end = Offset(centerX + 25f, barY),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
                // Draw Barbell Plates
                drawCircle(color = Color.White, radius = 8f, center = Offset(centerX - 25f, barY))
                drawCircle(color = Color.White, radius = 8f, center = Offset(centerX + 25f, barY))

                // Draw stick figure body lying flat
                drawLine(
                    color = TextGray,
                    start = Offset(centerX - 20f, centerY + 15f),
                    end = Offset(centerX + 20f, centerY + 15f),
                    strokeWidth = 4f
                )
                // Draw arms holding bar
                drawLine(
                    color = TextGray,
                    start = Offset(centerX - 15f, centerY + 15f),
                    end = Offset(centerX - 15f, barY),
                    strokeWidth = 3f
                )
                drawLine(
                    color = TextGray,
                    start = Offset(centerX + 15f, centerY + 15f),
                    end = Offset(centerX + 15f, barY),
                    strokeWidth = 3f
                )
            }
            
            // Pull Day (Arm Curling or Pullup motions)
            workoutType == "Pull" || workoutType == "Calisthenics" -> {
                // Arm Curl / Pullup Bar
                val barY = centerY - 25f
                drawLine(
                    color = Color.DarkGray,
                    start = Offset(centerX - 30f, barY),
                    end = Offset(centerX + 30f, barY),
                    strokeWidth = 4f
                )

                // Body climbing up
                val bodyYOffset = barY + 15f + (animationState * 15f)
                // Draw Head
                drawCircle(color = Color.White, radius = 6f, center = Offset(centerX, bodyYOffset - 10f))
                // Draw Spine
                drawLine(
                    color = TextGray,
                    start = Offset(centerX, bodyYOffset - 4f),
                    end = Offset(centerX, bodyYOffset + 15f),
                    strokeWidth = 4f
                )
                // Draw Arms hanging
                drawLine(
                    color = SigmaGreen,
                    start = Offset(centerX - 10f, barY),
                    end = Offset(centerX, bodyYOffset - 2f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = SigmaGreen,
                    start = Offset(centerX + 10f, barY),
                    end = Offset(centerX, bodyYOffset - 2f),
                    strokeWidth = 3f
                )
            }

            // Leg Day (Squat motions)
            else -> {
                // Hip/Spine Squatting Offset
                val hipsY = centerY + (animationState * 18f)
                val headY = hipsY - 20f
                
                // Draw head
                drawCircle(color = Color.White, radius = 6f, center = Offset(centerX, headY))
                // Draw Spine
                drawLine(
                    color = TextGray,
                    start = Offset(centerX, headY + 6f),
                    end = Offset(centerX, hipsY),
                    strokeWidth = 4f
                )
                
                // Draw thighs squatted
                val kneeY = centerY + 15f
                drawLine(
                    color = SigmaOrange,
                    start = Offset(centerX, hipsY),
                    end = Offset(centerX - 12f, kneeY),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                // Draw shins
                drawLine(
                    color = SigmaOrange,
                    start = Offset(centerX - 12f, kneeY),
                    end = Offset(centerX - 12f, centerY + 28f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
