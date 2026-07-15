package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkoutLog
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToTab: (Int) -> Unit, // Allows clicking dashboard metrics to navigate to corresponding tabs
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsState()
    val workoutsToday by viewModel.workoutsToday.collectAsState()
    val waterToday by viewModel.waterLogsToday.collectAsState()
    val foodToday by viewModel.foodLogsToday.collectAsState()
    val sleepToday by viewModel.sleepToday.collectAsState()
    val habitToday by viewModel.habitToday.collectAsState()

    // Motivation Quotes Database
    val motivationQuotes = remember {
        listOf(
            "Disiplin mengalahkan motivasi saat Anda merasa malas. Bangun dan angkat beban!",
            "Tubuh Anda bisa bertahan melakukan apa saja. Pikiran Andalah yang harus Anda yakinkan.",
            "Rasa sakit saat berolahraga hanya sementara, sedangkan penyesalan bertahan selamanya.",
            "Anda tidak mendapatkan tubuh ideal dengan berharap, Anda mendapatkannya dengan bekerja keras.",
            "Sigma Rule #1: Latihlah tubuhmu sekeras kamu melatih pikiranmu. Train smarter, become stronger!",
            "Kesehatan adalah satu-satunya investasi yang tidak pernah merugi.",
            "Konsistensi harian adalah kunci utama pembentukan fisik ksatria."
        )
    }
    val randomQuote = remember { motivationQuotes[java.util.Random().nextInt(motivationQuotes.size)] }

    // Nutrient Tally Calculations
    val consumedCalories = foodToday.sumOf { it.calories }
    val consumedProtein = foodToday.sumOf { it.proteinGrams.toDouble() }.toFloat()

    val bmr = viewModel.calculateBmr(profile)
    val tdee = viewModel.calculateTdee(bmr, profile.activityLevel)
    val targetCalories = when (profile.fitnessGoal.lowercase()) {
        "build muscle", "bulking" -> tdee + 400
        "lose weight", "cutting" -> tdee - 450
        else -> tdee
    }
    val targetProtein = viewModel.calculateProteinRequirement(profile)

    val currentWaterTotal = waterToday.sumOf { it.amountMl }
    val targetWaterMl = 3000 // 3.0 Liters default

    val bmi = viewModel.calculateBmi(profile.weight, profile.height)
    val bodyFat = viewModel.calculateBodyFat(
        heightCm = profile.height,
        waistCm = 80f, // estimation placeholder for dashboard
        neckCm = 38f,
        gender = profile.gender
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Welcome Header Block
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Selamat Datang,",
                    fontSize = 14.sp,
                    color = TextGray
                )
                Text(
                    text = profile.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Goal: ${profile.fitnessGoal}",
                    fontSize = 12.sp,
                    color = SigmaGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            // User Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(SigmaOrange, SigmaOrangeLight)))
                    .clickable { onNavigateToTab(3) }, // Go to Profile settings
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile avatar",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Daily Motivation Quote
        Card(
            colors = CardDefaults.cardColors(containerColor = CarbonCardElevated),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = SigmaOrange,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "MOTIVASI SIGMA HARI INI",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SigmaOrange,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "\"$randomQuote\"",
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Three Metrics Grid (Weight, BMI, Body Fat)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Weight Widget
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonCard),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToTab(3) } // Go to Progress/Logs
                    .testTag("weight_card"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = SigmaOrange)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Berat Badan", fontSize = 11.sp, color = TextGray)
                    Text("${profile.weight} kg", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Target: ${profile.targetWeight} kg", fontSize = 9.sp, color = SigmaOrange)
                }
            }

            // BMI Widget
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonCard),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToTab(2) } // Go to Calculators
                    .testTag("bmi_card"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = SigmaGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("BMI", fontSize = 11.sp, color = TextGray)
                    Text(String.format("%.1f", bmi), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(viewModel.getBmiCategory(bmi), fontSize = 9.sp, color = SigmaGreen, textAlign = TextAlign.Center)
                }
            }

            // Body Fat Widget
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonCard),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToTab(2) } // Go to Calculators
                    .testTag("body_fat_card"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Accessibility, contentDescription = null, tint = WaterBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Body Fat", fontSize = 11.sp, color = TextGray)
                    Text(String.format("%.1f%%", bodyFat), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(viewModel.getBodyFatCategory(bodyFat, profile.gender), fontSize = 9.sp, color = WaterBlue)
                }
            }
        }

        // Daily Calories & Protein Rings/Bars Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CarbonCard),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "NUTRITION SUMMARY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SigmaOrange,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    // Calories Tracker
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Kalori Hari Ini", fontSize = 13.sp, color = Color.White)
                            Text("${consumedCalories}/${targetCalories.toInt()} kkal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SigmaOrange)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val calorieProgress = if (targetCalories > 0) (consumedCalories / targetCalories).coerceIn(0f, 1f) else 0f
                        LinearProgressIndicator(
                            progress = { calorieProgress },
                            color = SigmaOrange,
                            trackColor = Color(0xFF3E2723),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Protein Tracker
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Protein Hari Ini", fontSize = 13.sp, color = Color.White)
                            Text("${consumedProtein.toInt()}/${targetProtein.toInt()} g", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SigmaGreen)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val proteinProgress = if (targetProtein > 0) (consumedProtein / targetProtein).coerceIn(0f, 1f) else 0f
                        LinearProgressIndicator(
                            progress = { proteinProgress },
                            color = SigmaGreen,
                            trackColor = Color(0xFF1B5E20),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }

        // Water Tracker Block
        Card(
            colors = CardDefaults.cardColors(containerColor = CarbonCard),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("water_tracker_section")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WATER TRACKER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaterBlue,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Target: ${String.format("%.1f", targetWaterMl / 1000f)} Liter",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                    IconButton(onClick = { viewModel.resetWater() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Water", tint = TextGray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Water Stats & Progress
                val waterProgress = (currentWaterTotal.toFloat() / targetWaterMl).coerceIn(0f, 1f)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$currentWaterTotal ml",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "${(waterProgress * 100).toInt()}% Tercapai",
                        fontSize = 13.sp,
                        color = WaterBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { waterProgress },
                    color = WaterBlue,
                    trackColor = Color(0xFF003C5C),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Add buttons (+250ml, +500ml, +1000ml)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.addWater(250) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0072A0)),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("add_water_250_btn")
                    ) {
                        Text("+250 ml", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.addWater(500) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0083B8)),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("add_water_500_btn")
                    ) {
                        Text("+500 ml", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.addWater(1000) },
                        colors = ButtonDefaults.buttonColors(containerColor = WaterBlue),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(40.dp)
                            .testTag("add_water_1000_btn")
                    ) {
                        Text("+1.0 Liter", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Today's Workouts List Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CarbonCard),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "WORKOUTS LOGGED TODAY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SigmaOrange,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                if (workoutsToday.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = TextGray, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada latihan yang dicatat hari ini.",
                            fontSize = 12.sp,
                            color = TextGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { onNavigateToTab(1) }) {
                            Text("Buka Program Latihan", color = SigmaOrange, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    workoutsToday.forEach { log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .background(CarbonCardElevated, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(log.exerciseName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                Text("${log.sets} Set x ${log.reps} Reps | ${log.weightKg} kg", fontSize = 12.sp, color = TextGray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${log.caloriesBurned} kkal", fontWeight = FontWeight.Bold, color = SigmaGreen, fontSize = 13.sp)
                                Text("${log.durationMinutes} mnt", fontSize = 11.sp, color = TextGray)
                            }
                        }
                    }
                }
            }
        }

        // Habit Tracker Checklist Widget
        Card(
            colors = CardDefaults.cardColors(containerColor = CarbonCard),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "HABIT TRACKER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SigmaGreen,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                val habits = listOf(
                    Triple("workout", "Workout Harian", habitToday.workoutChecked),
                    Triple("water", "Minum Air (2-5 L)", habitToday.waterChecked),
                    Triple("protein", "Protein Cukup", habitToday.proteinChecked),
                    Triple("sleep", "Tidur Cukup (>= 7 Jam)", habitToday.sleepChecked),
                    Triple("stretching", "Stretching / Peregangan", habitToday.stretchingChecked),
                    Triple("cardio", "Cardio Ringan", habitToday.cardioChecked)
                )

                habits.forEach { (type, title, isChecked) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleHabit(type) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { viewModel.toggleHabit(type) },
                            colors = CheckboxDefaults.colors(checkedColor = SigmaGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            color = if (isChecked) TextGray else Color.White,
                            style = if (isChecked) MaterialTheme.typography.bodyMedium.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Sleep Analysis Placeholder
        Card(
            colors = CardDefaults.cardColors(containerColor = CarbonCard),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "SLEEP TRACKER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = QualityPurple,
                        letterSpacing = 1.sp
                    )
                    if (sleepToday != null) {
                        Text("Tidur semalam: ${sleepToday?.durationHours} Jam", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Kualitas tidur: ${sleepToday?.qualityScore}%", fontSize = 12.sp, color = TextGray)
                    } else {
                        Text("Belum mencatat waktu tidur hari ini.", fontSize = 12.sp, color = TextGray)
                    }
                }
                TextButton(onClick = { onNavigateToTab(3) }) { // Navigate to Progress Logs & remind
                    Text("Catat Tidur", color = QualityPurple, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
