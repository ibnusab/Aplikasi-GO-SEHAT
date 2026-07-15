package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun AiCoachScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val aiRecommendation by viewModel.aiRecommendation.collectAsState()
    val aiRecLoading by viewModel.aiRecommendationLoading.collectAsState()
    
    val aiFoodRecognition by viewModel.aiFoodRecognition.collectAsState()
    val aiFoodLoading by viewModel.aiFoodRecognitionLoading.collectAsState()
    
    val profile by viewModel.profile.collectAsState()
    
    var activeAiTab by remember { mutableIntStateOf(0) }
    var foodDescriptionInput by remember { mutableStateOf("") }
    


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonDark)
    ) {
        // AI Sub Tabs
        TabRow(
            selectedTabIndex = activeAiTab,
            containerColor = CarbonCard,
            contentColor = SigmaOrange,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeAiTab]),
                    color = SigmaOrange
                )
            }
        ) {
            Tab(
                selected = activeAiTab == 0,
                onClick = { activeAiTab = 0 },
                text = { Text("AI Personal Trainer", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                selectedContentColor = SigmaOrange,
                unselectedContentColor = TextGray,
                modifier = Modifier.testTag("ai_coach_tab_trainer")
            )
            Tab(
                selected = activeAiTab == 1,
                onClick = { activeAiTab = 1 },
                text = { Text("AI Food Recognizer", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                selectedContentColor = SigmaOrange,
                unselectedContentColor = TextGray,
                modifier = Modifier.testTag("ai_coach_tab_food")
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AI Header Explainer
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonCardElevated),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = SigmaGreen,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PREMIUM GO SEHAT AI COACH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SigmaGreen,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Didukung oleh model cerdas Google Gemini 3.5 Flash untuk analisis gizi dan rekomendasi program latihan presisi tinggi.",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            }

            if (activeAiTab == 0) {
                // --- TAB 1: AI PERSONAL TRAINER ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = CarbonCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "REKOMENDASI PROGRAM LATIHAN PERSONAL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SigmaOrange,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Sistem AI akan mengevaluasi berat badan Anda saat ini (${profile.weight} kg), target Anda (${profile.targetWeight} kg), tingkat aktivitas (${profile.activityLevel}), serta tujuan fitness Anda (${profile.fitnessGoal}) untuk memformulasikan program latihan spesifik.",
                            fontSize = 12.sp,
                            color = TextGray
                        )

                        if (aiRecLoading) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = SigmaOrange)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Mengevaluasi fisik & program kecerdasan...", fontSize = 12.sp, color = TextGray)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.getAiWorkoutRecommendations() },
                                colors = ButtonDefaults.buttonColors(containerColor = SigmaOrange),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("generate_ai_recommendations_btn")
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generasikan Program AI Anda", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        if (aiRecommendation.isNotEmpty() && !aiRecLoading) {
                            HorizontalDivider(color = CarbonCardElevated)
                            
                            // Visual AI Container Output
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = CarbonDark,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "HASIL REKOMENDASI AI COACH:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SigmaGreen,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = aiRecommendation,
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // --- TAB 2: AI FOOD RECOGNIZER ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = CarbonCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "AI FOOD RECOGNIZER & GIZI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaterBlue,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Ketik deskripsi menu makanan Anda (misal: 'Nasi goreng kambing + telur dadar minyak sedikit' atau 'Dada ayam bakar bumbu kecap') atau simulasikan jepret kamera.",
                            fontSize = 11.sp,
                            color = TextGray
                        )

                        OutlinedTextField(
                            value = foodDescriptionInput,
                            onValueChange = { foodDescriptionInput = it },
                            placeholder = { Text("Contoh: Nasi uduk lauk ayam suwir tempe orek...") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WaterBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_food_desc_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Camera simulate action button
                            OutlinedButton(
                                onClick = {
                                    foodDescriptionInput = "Sate Ayam Madura (8 Tusuk) + Lontong"
                                    // Simulated Bitmap factory to pass a drawable to the AI analysis
                                    val fakeBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                                    viewModel.analyzeFoodImage("Sate Ayam Madura (8 Tusuk) + Lontong", fakeBitmap)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("simulate_food_camera_btn")
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = SigmaGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Jepret Makanan", fontSize = 11.sp)
                            }

                            // Generate analyze trigger button
                            Button(
                                onClick = {
                                    if (foodDescriptionInput.isNotEmpty()) {
                                        viewModel.analyzeFoodImage(foodDescriptionInput, null)
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WaterBlue),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("analyze_food_text_btn")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analisis Gizi", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (aiFoodLoading) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = WaterBlue)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Mengestimasi kalori & kandungan gizi makro...", fontSize = 12.sp, color = TextGray)
                            }
                        }

                        if (aiFoodRecognition.isNotEmpty() && !aiFoodLoading) {
                            HorizontalDivider(color = CarbonCardElevated)
                            
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = CarbonDark,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "HASIL DETEKSI GIZI:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WaterBlue,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = aiFoodRecognition,
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
