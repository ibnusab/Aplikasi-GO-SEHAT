package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodLog
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun NutritionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val foodLogsToday by viewModel.foodLogsToday.collectAsState()
    
    var selectedMealTypePlanner by remember { mutableStateOf("Breakfast") }
    var searchQueryFood by remember { mutableStateOf("") }
    
    // Food entry inputs for manual custom add
    var customFoodName by remember { mutableStateOf("") }
    var customCalories by remember { mutableStateOf("150") }
    var customProtein by remember { mutableStateOf("10") }
    var customCarbs by remember { mutableStateOf("20") }
    var customFats by remember { mutableStateOf("5") }

    // Built-in Indonesian Food Database
    val indonesianFoods = listOf(
        IndoFood("Nasi Putih", 130, 2.4f, 28f, 0.2f),
        IndoFood("Dada Ayam Panggang", 165, 31f, 0f, 3.6f),
        IndoFood("Ayam Goreng (Satu Porsi)", 260, 22f, 8f, 16f),
        IndoFood("Telur Rebus (Satu Butir)", 78, 6.3f, 0.6f, 5.3f),
        IndoFood("Tempe Goreng (2 Potong)", 120, 8f, 10f, 6.8f),
        IndoFood("Tahu Goreng (2 Potong)", 90, 7f, 4f, 5f),
        IndoFood("Pisang Sunpride", 90, 1.2f, 23f, 0.3f),
        IndoFood("Oatmeal Mangkuk", 150, 5f, 27f, 2.5f),
        IndoFood("Susu Low Fat (Gelas)", 110, 8f, 12f, 2f),
        IndoFood("Putih Telur (5 Butir)", 85, 20f, 1f, 0f)
    )

    val meals = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    // Calculations
    val mealLogs = foodLogsToday.filter { it.mealType == selectedMealTypePlanner }
    val totalCaloriesToday = foodLogsToday.sumOf { it.calories }
    val totalProteinToday = foodLogsToday.sumOf { it.proteinGrams.toDouble() }.toFloat()
    val totalCarbsToday = foodLogsToday.sumOf { it.carbsGrams.toDouble() }.toFloat()
    val totalFatsToday = foodLogsToday.sumOf { it.fatsGrams.toDouble() }.toFloat()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "MEAL PLANNER & NUTRITION",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Text(
                text = "Eat Better. Log makanan untuk mencapai keseimbangan nutrisi.",
                fontSize = 12.sp,
                color = TextGray
            )
        }

        // Tally Dashboard
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("TOTAL NUTRISI HARI INI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SigmaOrange)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Kalori", fontSize = 12.sp, color = TextGray)
                            Text("$totalCaloriesToday kkal", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            MacronutrientTally(label = "P", amt = "${totalProteinToday.toInt()}g", color = SigmaGreen)
                            MacronutrientTally(label = "C", amt = "${totalCarbsToday.toInt()}g", color = WaterBlue)
                            MacronutrientTally(label = "F", amt = "${totalFatsToday.toInt()}g", color = SigmaOrange)
                        }
                    }
                }
            }
        }

        // Meal Category Selection Tab
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                meals.forEach { meal ->
                    val isSelected = selectedMealTypePlanner == meal
                    Button(
                        onClick = { selectedMealTypePlanner = meal },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) SigmaOrange else CarbonCard,
                            contentColor = if (isSelected) Color.White else TextGray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("meal_tab_$meal")
                    ) {
                        Text(meal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Meal Planner Log List for active tab
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DAFTAR MAKANAN: $selectedMealTypePlanner",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SigmaGreen,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (mealLogs.isEmpty()) {
                        Text(
                            text = "Belum mencatat makanan untuk $selectedMealTypePlanner.",
                            fontSize = 12.sp,
                            color = TextGray,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        mealLogs.forEach { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(CarbonCardElevated, RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(log.foodName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    Text("P: ${log.proteinGrams.toInt()}g | C: ${log.carbsGrams.toInt()}g | F: ${log.fatsGrams.toInt()}g", fontSize = 11.sp, color = TextGray)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${log.calories} kkal", fontWeight = FontWeight.Bold, color = SigmaOrange, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = Color.Red,
                                        modifier = Modifier
                                            .clickable { viewModel.deleteFood(log) }
                                            .size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Indonesian Database Search Header
        item {
            Text(
                text = "DATABASE MAKANAN INDONESIA",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = searchQueryFood,
                onValueChange = { searchQueryFood = it },
                placeholder = { Text("Cari nasi, ayam, tempe...", color = TextGray) },
                leadingIcon = { Icon(Icons.Default.Fastfood, contentDescription = null, tint = SigmaOrange) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange, unfocusedBorderColor = CarbonCardElevated, unfocusedContainerColor = CarbonCard, focusedContainerColor = CarbonCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("food_search_bar")
            )
        }

        // Search list of Database
        val filteredFoods = indonesianFoods.filter {
            searchQueryFood.isEmpty() || it.name.lowercase().contains(searchQueryFood.lowercase())
        }

        items(filteredFoods) { food ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CarbonCard, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(food.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Text("${food.calories} kkal | P: ${food.p}g C: ${food.c}g F: ${food.f}g", fontSize = 11.sp, color = TextGray)
                }

                Button(
                    onClick = {
                        viewModel.addFoodLog(
                            mealType = selectedMealTypePlanner,
                            foodName = food.name,
                            calories = food.calories,
                            protein = food.p,
                            carbs = food.c,
                            fats = food.f
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SigmaGreen),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Manual Add Form Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "MAKANAN CUSTOM BARU",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SigmaOrange,
                        letterSpacing = 1.sp
                    )

                    OutlinedTextField(
                        value = customFoodName,
                        onValueChange = { customFoodName = it },
                        label = { Text("Nama Makanan") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_food_name_input")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customCalories,
                            onValueChange = { customCalories = it },
                            label = { Text("Kkal") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )
                        OutlinedTextField(
                            value = customProtein,
                            onValueChange = { customProtein = it },
                            label = { Text("Protein (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customCarbs,
                            onValueChange = { customCarbs = it },
                            label = { Text("Carbs (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )
                        OutlinedTextField(
                            value = customFats,
                            onValueChange = { customFats = it },
                            label = { Text("Fats (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SigmaOrange)
                        )
                    }

                    Button(
                        onClick = {
                            if (customFoodName.isNotEmpty()) {
                                viewModel.addFoodLog(
                                    mealType = selectedMealTypePlanner,
                                    foodName = customFoodName,
                                    calories = customCalories.toIntOrNull() ?: 150,
                                    protein = customProtein.toFloatOrNull() ?: 10f,
                                    carbs = customCarbs.toFloatOrNull() ?: 20f,
                                    fats = customFats.toFloatOrNull() ?: 5f
                                )
                                customFoodName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SigmaOrange),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("add_custom_food_btn")
                    ) {
                        Text("Log Makanan Custom", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun MacronutrientTally(label: String, amt: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier
                .background(color.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
        Text(amt, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

private data class IndoFood(val name: String, val calories: Int, val p: Float, val c: Float, val f: Float)
