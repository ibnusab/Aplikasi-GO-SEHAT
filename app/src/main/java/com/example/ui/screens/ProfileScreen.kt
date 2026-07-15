package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsState()
    val reminderSettings by viewModel.reminderSettings.collectAsState()

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var targetWeight by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("Moderate") }
    var fitnessGoal by remember { mutableStateOf("Build Muscle") }

    // Alarm states
    var workoutEnabled by remember { mutableStateOf(true) }
    var waterEnabled by remember { mutableStateOf(true) }
    var snackbarVisible by remember { mutableStateOf(false) }

    LaunchedEffect(profile) {
        name = profile.name
        age = profile.age.toString()
        gender = profile.gender
        height = profile.height.toString()
        weight = profile.weight.toString()
        targetWeight = profile.targetWeight.toString()
        activityLevel = profile.activityLevel
        fitnessGoal = profile.fitnessGoal
    }

    LaunchedEffect(reminderSettings) {
        workoutEnabled = reminderSettings.workoutEnabled
        waterEnabled = reminderSettings.waterEnabled
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "DATA PENGGUNA & SETTING",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Text(
                text = "Konfigurasikan profil fisik dan target GO SEHAT Anda.",
                fontSize = 12.sp,
                color = TextGray
            )

            // Main Settings Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Lengkap") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Age
                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it },
                            label = { Text("Umur (tahun)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_age_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )

                        // Gender Selection
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Jenis Kelamin", fontSize = 11.sp, color = TextGray)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val isMale = gender.lowercase() == "male"
                                FilterChip(
                                    selected = isMale,
                                    onClick = { gender = "Male" },
                                    label = { Text("Laki") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SigmaOrange,
                                        selectedLabelColor = Color.White
                                    )
                                )
                                FilterChip(
                                    selected = !isMale,
                                    onClick = { gender = "Female" },
                                    label = { Text("Perempuan") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SigmaOrange,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Height
                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it },
                            label = { Text("Tinggi (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )

                        // Weight
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it },
                            label = { Text("Berat (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )
                    }

                    // Target Weight
                    OutlinedTextField(
                        value = targetWeight,
                        onValueChange = { targetWeight = it },
                        label = { Text("Target Berat Badan (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                    )

                    // Activity Level Dropdown approximation using Segmented Chips
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Tingkat Aktivitas", fontSize = 12.sp, color = TextGray)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            val list = listOf("Sedentary", "Light", "Moderate", "Active")
                            list.forEach { level ->
                                FilterChip(
                                    selected = activityLevel == level,
                                    onClick = { activityLevel = level },
                                    label = { Text(level, fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SigmaGreen, selectedLabelColor = Color.Black)
                                )
                            }
                        }
                    }

                    // Fitness Goals Segmented Chips
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Tujuan Kebugaran", fontSize = 12.sp, color = TextGray)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            val goals = listOf("Build Muscle", "Lose Weight", "Maintain", "Calisthenics")
                            goals.forEach { goal ->
                                FilterChip(
                                    selected = fitnessGoal == goal,
                                    onClick = { fitnessGoal = goal },
                                    label = { Text(goal, fontSize = 9.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SigmaOrange, selectedLabelColor = Color.White)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val h = height.toFloatOrNull() ?: profile.height
                            val w = weight.toFloatOrNull() ?: profile.weight
                            val tw = targetWeight.toFloatOrNull() ?: profile.targetWeight
                            val a = age.toIntOrNull() ?: profile.age
                            viewModel.updateProfile(
                                name = name,
                                age = a,
                                gender = gender,
                                height = h,
                                weight = w,
                                targetWeight = tw,
                                activityLevel = activityLevel,
                                fitnessGoal = fitnessGoal
                            )
                            snackbarVisible = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SigmaGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_profile_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan Perubahan", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }

            // Reminders Configuration Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "NOTIFIKASI REMINDER SIGMA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = WaterBlue,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Pengingat Workout", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Notifikasi harian untuk sesi latihan Anda.", fontSize = 11.sp, color = TextGray)
                        }
                        Switch(
                            checked = workoutEnabled,
                            onCheckedChange = {
                                workoutEnabled = it
                                viewModel.updateReminderSettings(
                                    workoutEnabled = it,
                                    workoutTime = reminderSettings.workoutTime,
                                    waterEnabled = waterEnabled,
                                    waterInterval = reminderSettings.waterIntervalHours,
                                    mealEnabled = reminderSettings.mealEnabled,
                                    mealTimes = reminderSettings.mealTimes,
                                    sleepEnabled = reminderSettings.sleepEnabled,
                                    sleepTime = reminderSettings.sleepTime,
                                    weightEnabled = reminderSettings.weightEnabled,
                                    weightTime = reminderSettings.weightTime
                                )
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = SigmaOrange, checkedTrackColor = Color(0xFF6D2000))
                        )
                    }

                    Divider(color = CarbonCardElevated)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Pengingat Minum Air", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Notifikasi rutin setiap 2 jam sekali.", fontSize = 11.sp, color = TextGray)
                        }
                        Switch(
                            checked = waterEnabled,
                            onCheckedChange = {
                                waterEnabled = it
                                viewModel.updateReminderSettings(
                                    workoutEnabled = workoutEnabled,
                                    workoutTime = reminderSettings.workoutTime,
                                    waterEnabled = it,
                                    waterInterval = reminderSettings.waterIntervalHours,
                                    mealEnabled = reminderSettings.mealEnabled,
                                    mealTimes = reminderSettings.mealTimes,
                                    sleepEnabled = reminderSettings.sleepEnabled,
                                    sleepTime = reminderSettings.sleepTime,
                                    weightEnabled = reminderSettings.weightEnabled,
                                    weightTime = reminderSettings.weightTime
                                )
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = WaterBlue, checkedTrackColor = Color(0xFF003855))
                        )
                    }
                }
            }

            // Log Out Button
            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("logout_profile_btn")
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout dari Akun", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Floating snackbar confirmation
        if (snackbarVisible) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp, start = 16.dp, end = 16.dp),
                action = {
                    TextButton(onClick = { snackbarVisible = false }) {
                        Text("OK", color = SigmaGreen)
                    }
                },
                containerColor = CarbonCardElevated,
                contentColor = Color.White
            ) {
                Text("Profil Sigma Berhasil Diperbarui!")
            }
        }
    }
}
