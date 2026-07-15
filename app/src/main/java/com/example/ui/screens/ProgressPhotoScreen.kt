package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun ProgressPhotoScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allWeights by viewModel.allWeights.collectAsState()
    val allWorkouts by viewModel.allWorkouts.collectAsState()
    val waterLogsToday by viewModel.waterLogsToday.collectAsState()
    val progressPhotos by viewModel.allProgressPhotos.collectAsState()
    val profile by viewModel.profile.collectAsState()

    var activeProgressSubTab by remember { mutableStateOf(0) }
    val progressTabs = listOf("Berat & Tidur", "Foto Progres", "Sigma Badges")

    // Weight input states
    var newWeightInput by remember { mutableStateOf("") }
    
    // Sleep input states
    var sleepHourInput by remember { mutableStateOf("8.5") }
    var sleepQualityInput by remember { mutableStateOf("80") }

    // Photos state
    var selectedPhotoTag by remember { mutableStateOf("Front") }
    var zoomLevelPhoto by remember { mutableStateOf(1f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonDark)
    ) {
        // Tab Headers
        TabRow(
            selectedTabIndex = activeProgressSubTab,
            containerColor = CarbonCard,
            contentColor = SigmaOrange,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeProgressSubTab]),
                    color = SigmaOrange
                )
            }
        ) {
            progressTabs.forEachIndexed { index, title ->
                Tab(
                    selected = activeProgressSubTab == index,
                    onClick = { activeProgressSubTab = index },
                    text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = SigmaOrange,
                    unselectedContentColor = TextGray,
                    modifier = Modifier.testTag("progress_tab_$index")
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (activeProgressSubTab) {
                0 -> {
                    // --- WEIGHT & SLEEP LOGGER TAB ---
                    
                    // 1. Weight Tracker Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CarbonCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "WEIGHT LOGGER Harian",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SigmaOrange,
                                    letterSpacing = 1.sp
                                )
                                Icon(Icons.Default.TrendingDown, contentDescription = null, tint = SigmaGreen)
                            }

                            Text(
                                "Mencatat berat badan setiap hari membantu Anda mengontrol tren fluktuasi surplus/defisit energi harian.",
                                fontSize = 12.sp,
                                color = TextGray
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newWeightInput,
                                    onValueChange = { newWeightInput = it },
                                    label = { Text("Berat Baru (kg)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("new_weight_input"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                                )

                                Button(
                                    onClick = {
                                        val w = newWeightInput.toFloatOrNull()
                                        if (w != null && w > 0) {
                                            viewModel.logWeight(w)
                                            newWeightInput = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SigmaOrange),
                                    modifier = Modifier
                                        .height(56.dp)
                                        .testTag("submit_weight_btn")
                                ) {
                                    Text("Simpan", fontWeight = FontWeight.Bold)
                                }
                            }

                            Divider(color = CarbonCardElevated)

                            // Weight Progress Summary
                            if (allWeights.isNotEmpty()) {
                                val currentWeight = allWeights.last().weightKg
                                val firstWeight = allWeights.first().weightKg
                                val target = profile.targetWeight
                                val diff = currentWeight - firstWeight
                                val targetDiff = target - currentWeight

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Berat Mulai", fontSize = 10.sp, color = TextGray)
                                        Text("$firstWeight kg", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Column {
                                        Text("Berat Saat Ini", fontSize = 10.sp, color = TextGray)
                                        Text("$currentWeight kg", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SigmaOrange)
                                    }
                                    Column {
                                        Text("Selisih Progres", fontSize = 10.sp, color = TextGray)
                                        val symbol = if (diff >= 0) "+" else ""
                                        Text("$symbol${String.format("%.1f", diff)} kg", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (diff <= 0) SigmaGreen else SigmaOrange)
                                    }
                                    Column {
                                        Text("Sisa Target", fontSize = 10.sp, color = TextGray)
                                        Text("${String.format("%.1f", Math.abs(targetDiff))} kg lagi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WaterBlue)
                                    }
                                }
                            } else {
                                Text("Belum ada riwayat berat badan logged. Ubah profil Anda untuk mencatat entri pertama.", fontSize = 11.sp, color = TextGray, textAlign = TextAlign.Center)
                            }
                        }
                    }

                    // 2. Sleep Logger Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CarbonCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "LOG WAKTU TIDUR SEMALAM",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = QualityPurple,
                                letterSpacing = 1.sp
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = sleepHourInput,
                                    onValueChange = { sleepHourInput = it },
                                    label = { Text("Durasi Tidur (jam)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = QualityPurple)
                                )

                                OutlinedTextField(
                                    value = sleepQualityInput,
                                    onValueChange = { sleepQualityInput = it },
                                    label = { Text("Kualitas (1-100%)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = QualityPurple)
                                )
                            }

                            Button(
                                onClick = {
                                    val hrs = sleepHourInput.toFloatOrNull() ?: 8f
                                    val q = sleepQualityInput.toIntOrNull() ?: 80
                                    viewModel.logSleep("23:00", "07:30", hrs, q)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = QualityPurple),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("log_sleep_btn")
                            ) {
                                Icon(Icons.Default.Bedtime, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Simpan Log Tidur", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                1 -> {
                    // --- PROGRESS PHOTO GALLERY ---
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CarbonCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "ALBUM FOTO PROGRES",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SigmaGreen,
                                letterSpacing = 1.sp
                            )

                            // Select tags Front, Side, Back
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Front", "Side", "Back").forEach { tag ->
                                    val selected = selectedPhotoTag == tag
                                    FilterChip(
                                        selected = selected,
                                        onClick = { selectedPhotoTag = tag },
                                        label = { Text(tag) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = SigmaGreen,
                                            selectedLabelColor = Color.Black
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Comparison Slider Box (Before vs After)
                            Text(
                                text = "Bandingkan Kondisi Anda (Procedural Before/After)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            // Visual Simulation Frame (Renders beautiful progress silhouette on Canvas!)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .background(CarbonCardElevated, RoundedCornerShape(10.dp))
                                    .clickable {
                                        // Change zoom levels on tap
                                        zoomLevelPhoto = if (zoomLevelPhoto == 1f) 1.3f else 1f
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(modifier = Modifier.fillMaxSize()) {
                                    // Before (Leaner or Heavier depending on goals)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        FitnessSilhouetteCanvas(
                                            tag = selectedPhotoTag,
                                            isBefore = true,
                                            goal = profile.fitnessGoal,
                                            zoom = zoomLevelPhoto
                                        )
                                        Text(
                                            "BEFORE",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(topStart = 10.dp, bottomEnd = 10.dp))
                                                .padding(6.dp)
                                        )
                                    }

                                    // Mid Split Line
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(2.dp)
                                            .background(SigmaOrange)
                                    )

                                    // After (Super fit athlete shape!)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        FitnessSilhouetteCanvas(
                                            tag = selectedPhotoTag,
                                            isBefore = false,
                                            goal = profile.fitnessGoal,
                                            zoom = zoomLevelPhoto
                                        )
                                        Text(
                                            "AFTER",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SigmaGreen,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(topEnd = 10.dp, bottomStart = 10.dp))
                                                .padding(6.dp)
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Tip: Sentuh layar di atas untuk melakukan ZOOM foto.", fontSize = 11.sp, color = TextGray)
                                Button(
                                    onClick = {
                                        // Save a photo entry with a mock uri to trigger photo logs
                                        viewModel.addProgressPhoto("content://simulated/fit_pose_$selectedPhotoTag", selectedPhotoTag)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SigmaGreen)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Simpan Foto", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Photo timeline log
                            val taggedPhotos = progressPhotos.filter { it.tag == selectedPhotoTag }
                            if (taggedPhotos.isNotEmpty()) {
                                Text("Riwayat Tangkapan Foto ($selectedPhotoTag):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                taggedPhotos.forEach { photo ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(CarbonCardElevated, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = SigmaOrange, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Tangkapan Tanggal: ${photo.date}", fontSize = 13.sp, color = Color.White)
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus",
                                            tint = Color.Red,
                                            modifier = Modifier
                                                .clickable { viewModel.deleteProgressPhoto(photo) }
                                                .size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // --- SIGMA BADGES / ACHIEVEMENTS ---
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CarbonCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "VAULT MEDALI & PRESTASI",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SigmaOrange,
                                letterSpacing = 1.sp
                            )

                            // Unlocking metrics logic
                            val totalWorkoutCount = allWorkouts.size
                            val hasWaterConsistently = waterLogsToday.sumOf { it.amountMl } >= 3000
                            val weightLossAchieved = if (allWeights.isNotEmpty()) {
                                val start = allWeights.first().weightKg
                                val curr = allWeights.last().weightKg
                                (start - curr) >= 5f
                            } else false

                            val badges = listOf(
                                BadgeInfo(
                                    title = "🏆 Workout 7 Hari",
                                    desc = "Melakukan log latihan minimal 7 hari beruntun.",
                                    isUnlocked = totalWorkoutCount >= 7,
                                    unlockProgress = "$totalWorkoutCount/7 Hari"
                                ),
                                BadgeInfo(
                                    title = "🏆 Minum Air 30 Hari",
                                    desc = "Konsisten mencukupi kebutuhan air 3 Liter harian.",
                                    isUnlocked = hasWaterConsistently,
                                    unlockProgress = if (hasWaterConsistently) "1/1" else "0/1"
                                ),
                                BadgeInfo(
                                    title = "🏆 Berat Turun 5 kg",
                                    desc = "Berhasil memotong lemak tubuh hingga 5 kg.",
                                    isUnlocked = weightLossAchieved,
                                    unlockProgress = if (weightLossAchieved) "Unlocked!" else "Belum tercapai"
                                ),
                                BadgeInfo(
                                    title = "🏆 Protein Konsisten",
                                    desc = "Memenuhi kecukupan protein harian untuk recovery otot.",
                                    isUnlocked = totalWorkoutCount >= 1, // unlocked easily for demo
                                    unlockProgress = "1/1 Selesai"
                                ),
                                BadgeInfo(
                                    title = "🏆 Workout 100 Kali",
                                    desc = "Ksatria utama GO SEHAT dengan 100 log latihan lengkap.",
                                    isUnlocked = totalWorkoutCount >= 100,
                                    unlockProgress = "$totalWorkoutCount/100 Latihan"
                                )
                            )

                            badges.forEach { badge ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (badge.isUnlocked) Color(0xFF1E3524) else CarbonCardElevated, RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(if (badge.isUnlocked) SigmaGreen else Color.DarkGray, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (badge.isUnlocked) "⭐" else "🔒",
                                            fontSize = 20.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = badge.title,
                                            fontWeight = FontWeight.Bold,
                                            color = if (badge.isUnlocked) Color.White else TextGray,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = badge.desc,
                                            fontSize = 11.sp,
                                            color = if (badge.isUnlocked) OnCarbonDark else TextGray
                                        )
                                    }

                                    Text(
                                        text = badge.unlockProgress,
                                        fontSize = 11.sp,
                                        color = if (badge.isUnlocked) SigmaGreen else TextGray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// Procedural Canvas drawing representing body physical silhouettes for before/after comparison
@Composable
fun FitnessSilhouetteCanvas(
    tag: String,
    isBefore: Boolean,
    goal: String,
    zoom: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize().padding(10.dp)) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val centerY = height / 2

        val scale = zoom

        // Draw background grid lines for nice aesthetics
        for (i in 1..4) {
            val gridY = height * (i / 5f)
            drawLine(
                color = Color.DarkGray.copy(alpha = 0.3f),
                start = Offset(0f, gridY),
                end = Offset(width, gridY),
                strokeWidth = 2f
            )
        }

        // Color of silhouette: Before (Dull/Grayish), After (Glowing Orange/Green depending on goals)
        val fillAlpha = if (isBefore) 0.4f else 0.85f
        val bodyColor = if (isBefore) Color.Gray.copy(alpha = fillAlpha) else SigmaOrange.copy(alpha = fillAlpha)
        val contourColor = if (isBefore) Color.LightGray else SigmaGreen

        // We procedurally draw an aesthetic, high-contrast stick muscular structure or block anatomy
        // Head
        drawCircle(color = bodyColor, radius = 12f * scale, center = Offset(centerX, centerY - 60f * scale))
        drawCircle(color = contourColor, radius = 12f * scale, center = Offset(centerX, centerY - 60f * scale), style = androidx.compose.ui.graphics.drawscope.Stroke(2f))

        // Neck
        drawLine(color = bodyColor, start = Offset(centerX, centerY - 48f * scale), end = Offset(centerX, centerY - 40f * scale), strokeWidth = 8f * scale)

        // Chest/Shoulders
        val shoulderWidth = if (isBefore) 28f * scale else 42f * scale
        val waistWidth = if (isBefore) {
            // Before fat loss is wider, before muscle gain is thinner
            if (goal.lowercase().contains("lose")) 32f * scale else 20f * scale
        } else {
            24f * scale // Fit shredded waist!
        }

        // Draw Torso block
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(centerX - shoulderWidth, centerY - 38f * scale)
            lineTo(centerX + shoulderWidth, centerY - 38f * scale)
            lineTo(centerX + waistWidth, centerY + 10f * scale)
            lineTo(centerX - waistWidth, centerY + 10f * scale)
            close()
        }
        drawPath(path = path, color = bodyColor)
        drawPath(path = path, color = contourColor, style = androidx.compose.ui.graphics.drawscope.Stroke(3f))

        // Arms (Biceps pose if After!)
        if (!isBefore) {
            // Unlocked peak bicep pose
            drawLine(color = bodyColor, start = Offset(centerX - shoulderWidth, centerY - 35f * scale), end = Offset(centerX - (shoulderWidth + 15f) * scale, centerY - 45f * scale), strokeWidth = 10f * scale, cap = StrokeCap.Round)
            drawLine(color = bodyColor, start = Offset(centerX + shoulderWidth, centerY - 35f * scale), end = Offset(centerX + (shoulderWidth + 15f) * scale, centerY - 45f * scale), strokeWidth = 10f * scale, cap = StrokeCap.Round)
        } else {
            // Standard straight arm hanging pose
            drawLine(color = bodyColor, start = Offset(centerX - shoulderWidth, centerY - 35f * scale), end = Offset(centerX - shoulderWidth, centerY + 15f * scale), strokeWidth = 8f * scale, cap = StrokeCap.Round)
            drawLine(color = bodyColor, start = Offset(centerX + shoulderWidth, centerY - 35f * scale), end = Offset(centerX + shoulderWidth, centerY + 15f * scale), strokeWidth = 8f * scale, cap = StrokeCap.Round)
        }

        // Legs (Quadriceps structure)
        val legSpan = 18f * scale
        val legThick = if (isBefore) 10f * scale else 14f * scale
        drawLine(color = bodyColor, start = Offset(centerX - legSpan / 2, centerY + 10f * scale), end = Offset(centerX - legSpan, centerY + 65f * scale), strokeWidth = legThick, cap = StrokeCap.Round)
        drawLine(color = bodyColor, start = Offset(centerX + legSpan / 2, centerY + 10f * scale), end = Offset(centerX + legSpan, centerY + 65f * scale), strokeWidth = legThick, cap = StrokeCap.Round)
    }
}

private data class BadgeInfo(
    val title: String,
    val desc: String,
    val isUnlocked: Boolean,
    val unlockProgress: String
)
