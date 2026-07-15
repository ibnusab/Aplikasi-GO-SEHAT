package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun CalculatorsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("BMI", "Body Fat", "Kalori", "Protein")

    // Inputs
    var heightInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }
    var ageInput by remember { mutableStateOf("") }
    var genderSelection by remember { mutableStateOf("Male") }
    
    // Body Fat specific inputs
    var waistInput by remember { mutableStateOf("80") }
    var neckInput by remember { mutableStateOf("38") }
    var hipInput by remember { mutableStateOf("95") } // for female

    // Activity levels for Calorie Calc
    var selectedActivity by remember { mutableStateOf("Moderate") }
    val activityLevels = listOf("Sedentary", "Light", "Moderate", "Active", "Very Active")

    // Sync input with profile on load or profile change
    LaunchedEffect(profile) {
        if (heightInput.isEmpty()) heightInput = profile.height.toString()
        if (weightInput.isEmpty()) weightInput = profile.weight.toString()
        if (ageInput.isEmpty()) ageInput = profile.age.toString()
        genderSelection = profile.gender
        selectedActivity = profile.activityLevel
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonDark)
    ) {
        // Top Tab Indicator
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CarbonCard,
            contentColor = SigmaOrange,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = SigmaOrange
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = SigmaOrange,
                    unselectedContentColor = TextGray,
                    modifier = Modifier.testTag("calc_tab_$title")
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
            // General Demographic input card (shown across appropriate tabs)
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "DATA FISIK PENGGUNA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SigmaOrange,
                        letterSpacing = 1.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = heightInput,
                            onValueChange = { heightInput = it },
                            label = { Text("Tinggi (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("height_calc_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )

                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            label = { Text("Berat (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("weight_calc_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )
                    }

                    if (selectedTab == 1 || selectedTab == 2) { // Show Gender & Age for Body Fat and Calories
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = ageInput,
                                onValueChange = { ageInput = it },
                                label = { Text("Umur (tahun)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("age_calc_input"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                            )

                            // Gender Selector Row
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Jenis Kelamin", fontSize = 11.sp, color = TextGray)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val maleSelected = genderSelection.lowercase() == "male"
                                    ElevatedFilterChip(
                                        selected = maleSelected,
                                        onClick = { genderSelection = "Male" },
                                        label = { Text("Laki") },
                                        colors = FilterChipDefaults.elevatedFilterChipColors(
                                            selectedContainerColor = SigmaOrange,
                                            selectedLabelColor = Color.White
                                        ),
                                        modifier = Modifier.testTag("gender_male_chip")
                                    )
                                    ElevatedFilterChip(
                                        selected = !maleSelected,
                                        onClick = { genderSelection = "Female" },
                                        label = { Text("Perempuan") },
                                        colors = FilterChipDefaults.elevatedFilterChipColors(
                                            selectedContainerColor = SigmaOrange,
                                            selectedLabelColor = Color.White
                                        ),
                                        modifier = Modifier.testTag("gender_female_chip")
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val context = LocalContext.current
                    Button(
                        onClick = {
                            val h = heightInput.toFloatOrNull() ?: profile.height
                            val w = weightInput.toFloatOrNull() ?: profile.weight
                            val age = ageInput.toIntOrNull() ?: profile.age

                            viewModel.updateProfile(
                                name = profile.name,
                                age = age,
                                gender = genderSelection,
                                height = h,
                                weight = w,
                                targetWeight = profile.targetWeight,
                                activityLevel = if (selectedTab == 2) selectedActivity else profile.activityLevel,
                                fitnessGoal = profile.fitnessGoal
                            )
                            android.widget.Toast.makeText(context, "Profil fisik berhasil disimpan!", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SigmaOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("save_physical_data_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Simpan ke Profil Utama",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Tab Output Calculations
            when (selectedTab) {
                0 -> {
                    // --- BMI ---
                    val height = heightInput.toFloatOrNull() ?: 170f
                    val weight = weightInput.toFloatOrNull() ?: 65f
                    val bmi = if (height > 0) weight / ((height / 100f) * (height / 100f)) else 0f
                    val category = viewModel.getBmiCategory(bmi)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CarbonCardElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("HASIL KALKULATOR BMI", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SigmaGreen)
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = String.format("%.1f", bmi),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Text(
                                text = category,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SigmaGreen,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "BMI (Body Mass Index) mengukur perbandingan tinggi dan berat badan secara umum.",
                                fontSize = 12.sp,
                                color = TextGray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                1 -> {
                    // --- BODY FAT ---
                    OutlinedTextField(
                        value = waistInput,
                        onValueChange = { waistInput = it },
                        label = { Text("Lingkar Pinggang (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                    )

                    OutlinedTextField(
                        value = neckInput,
                        onValueChange = { neckInput = it },
                        label = { Text("Lingkar Leher (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                    )

                    if (genderSelection.lowercase() == "female") {
                        OutlinedTextField(
                            value = hipInput,
                            onValueChange = { hipInput = it },
                            label = { Text("Lingkar Pinggul (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )
                    }

                    val h = heightInput.toFloatOrNull() ?: 170f
                    val w = weightInput.toFloatOrNull() ?: 65f
                    val waist = waistInput.toFloatOrNull() ?: 80f
                    val neck = neckInput.toFloatOrNull() ?: 38f
                    val hip = hipInput.toFloatOrNull() ?: 95f

                    val bf = viewModel.calculateBodyFat(h, waist, neck, genderSelection, hip)
                    val fatCategory = viewModel.getBodyFatCategory(bf, genderSelection)
                    val fatMass = w * (bf / 100f)
                    val leanMass = w - fatMass

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CarbonCardElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("ESTIMASI PERSENTASE BODY FAT (US NAVY)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WaterBlue)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = String.format("%.1f%%", bf),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Text(
                                text = "Kategori: $fatCategory",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = WaterBlue,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("LEAN MASS", fontSize = 10.sp, color = TextGray)
                                    Text("${String.format("%.1f", leanMass)} kg", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("FAT MASS", fontSize = 10.sp, color = TextGray)
                                    Text("${String.format("%.1f", fatMass)} kg", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SigmaOrange)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // --- DAILY CALORIE CALCULATOR ---
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Tingkat Aktivitas Harian", fontSize = 12.sp, color = TextGray)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(activityLevels) { act ->
                                FilterChip(
                                    selected = selectedActivity == act,
                                    onClick = { selectedActivity = act },
                                    label = { Text(act) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SigmaOrange,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    val h = heightInput.toFloatOrNull() ?: 170f
                    val w = weightInput.toFloatOrNull() ?: 65f
                    val age = ageInput.toIntOrNull() ?: 22
                    
                    // Mifflin-St Jeor Formula
                    val bmr = if (genderSelection.lowercase() == "male") {
                        10f * w + 6.25f * h - 5f * age + 5f
                    } else {
                        10f * w + 6.25f * h - 5f * age - 161f
                    }

                    val factor = when (selectedActivity.lowercase()) {
                        "sedentary" -> 1.2f
                        "light" -> 1.375f
                        "moderate" -> 1.55f
                        "active" -> 1.725f
                        "very active" -> 1.9f
                        else -> 1.55f
                    }
                    val tdee = bmr * factor

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CarbonCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "ESTIMASI KEBUTUHAN ENERGI HARIAN",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SigmaOrange,
                                letterSpacing = 1.sp
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("BMR (Metabolisme Dasar)", fontSize = 11.sp, color = TextGray)
                                    Text("${bmr.toInt()} kkal/hari", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("TDEE (Energi Total)", fontSize = 11.sp, color = TextGray)
                                    Text("${tdee.toInt()} kkal/hari", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SigmaOrange)
                                }
                            }

                            Divider(color = CarbonCardElevated)

                            Text("Target Berdasarkan Rencana Anda:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Cutting (Fat Loss)", fontSize = 11.sp, color = TextGray)
                                    Text("${(tdee - 500).toInt()} kkal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF5350))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Maintenance", fontSize = 11.sp, color = TextGray)
                                    Text("${tdee.toInt()} kkal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WaterBlue)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Bulking (Muscle)", fontSize = 11.sp, color = TextGray)
                                    Text("${(tdee + 500).toInt()} kkal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SigmaGreen)
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // --- PROTEIN CALCULATOR ---
                    var fitnessGoalSelection by remember { mutableStateOf("Build Muscle") }
                    val fitnessGoals = listOf("Build Muscle", "Lose Weight", "Maintain", "Calisthenics")

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Fitness Goal", fontSize = 12.sp, color = TextGray)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(fitnessGoals) { goal ->
                                FilterChip(
                                    selected = fitnessGoalSelection == goal,
                                    onClick = { fitnessGoalSelection = goal },
                                    label = { Text(goal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SigmaGreen,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }
                    }

                    val w = weightInput.toFloatOrNull() ?: 65f
                    val multiplier = when (fitnessGoalSelection.lowercase()) {
                        "build muscle", "bulking" -> 2.2f
                        "lose weight", "cutting" -> 1.8f
                        "calisthenics" -> 2.0f
                        else -> 1.6f
                    }
                    val targetProtein = w * multiplier
                    // Fats calculation (approx 25% of a 2200 kkal budget = 550 kkal / 9 = 61g)
                    val targetFats = w * 1.0f
                    // Carbs approximation (approx remaining: 2200 - (protein*4) - (fat*9))
                    val targetCarbs = (2200f - (targetProtein * 4f) - (targetFats * 9f)) / 4f

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CarbonCardElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                text = "ESTIMASI TARGET MACRONUTRIENT HARIAN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SigmaGreen,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text("PROTEIN HARIAN", fontSize = 12.sp, color = TextGray)
                                Text("${targetProtein.toInt()} gram", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = SigmaGreen)
                                Text("Menyuplai pemulihan serat otot pasca-latihan.", fontSize = 11.sp, color = TextGray, textAlign = TextAlign.Center)
                            }

                            Divider(color = CarbonCard)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("KARBOHIDRAT", fontSize = 11.sp, color = TextGray)
                                    Text("${targetCarbs.toInt()} g", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("LEMAK SEHAT", fontSize = 11.sp, color = TextGray)
                                    Text("${targetFats.toInt()} g", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
