package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AuthState
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authState.collectAsState()
    
    var isRegistering by remember { mutableStateOf(false) }
    var isForgotPassword by remember { mutableStateOf(false) }
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Dialog for Fingerprint Verification
    var showBiometricDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CarbonDark, Color(0xFF0D0D0D))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header / Brand Branding
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = "GO SEHAT Logo",
                tint = SigmaOrange,
                modifier = Modifier
                    .size(72.dp)
                    .padding(bottom = 8.dp)
            )
            
            Text(
                text = "GO SEHAT",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Train Smarter. Eat Better. Become Stronger.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = SigmaGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Sub-Forms
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CarbonCard,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when {
                            isForgotPassword -> "Lupa Password"
                            isRegistering -> "Buat Akun Baru"
                            else -> "Selamat Datang Back"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Error Notification if exists
                    if (authState is AuthState.Error) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = (authState as AuthState.Error).message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Reset success notification if exists
                    if (authState is AuthState.ForgotPasswordSent) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "Link reset password telah dikirim ke: ${(authState as AuthState.ForgotPasswordSent).email}",
                                color = Color.White,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (isRegistering && !isForgotPassword) {
                        // Register Name
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nama Lengkap") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SigmaOrange) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SigmaOrange,
                                focusedLabelColor = SigmaOrange
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("name_input")
                                .padding(bottom = 12.dp)
                        )
                    }

                    // Email Input (needed in all forms)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SigmaOrange) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SigmaOrange,
                            focusedLabelColor = SigmaOrange
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input")
                            .padding(bottom = 12.dp)
                    )

                    if (!isForgotPassword) {
                        // Password Input
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SigmaOrange) },
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                val description = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = description, tint = TextGray)
                                }
                            },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SigmaOrange,
                                focusedLabelColor = SigmaOrange
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input")
                                .padding(bottom = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = SigmaOrange, modifier = Modifier.padding(16.dp))
                    } else {
                        // Submit Button
                        Button(
                            onClick = {
                                when {
                                    isForgotPassword -> {
                                        if (email.isNotEmpty()) viewModel.forgotPassword(email)
                                    }
                                    isRegistering -> {
                                        viewModel.register(name, email, password)
                                    }
                                    else -> {
                                        viewModel.login(email, password)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SigmaOrange),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_auth_button")
                        ) {
                            Text(
                                text = when {
                                    isForgotPassword -> "Kirim Link Reset"
                                    isRegistering -> "Daftar Sekarang"
                                    else -> "Login"
                                },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        // Text toggle options
                        if (!isForgotPassword) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isRegistering) "Sudah punya akun? Login disini" else "Belum punya akun? Daftar disini",
                                fontSize = 13.sp,
                                color = SigmaGreen,
                                modifier = Modifier
                                    .clickable {
                                        isRegistering = !isRegistering
                                        viewModel.resetAuthState()
                                    }
                                    .padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isForgotPassword) "Kembali ke Login" else "Lupa Password?",
                            fontSize = 13.sp,
                            color = TextGray,
                            modifier = Modifier
                                .clickable {
                                    isForgotPassword = !isForgotPassword
                                    isRegistering = false
                                    viewModel.resetAuthState()
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }

            // Google Login and Fingerprint Login Row
            if (!isForgotPassword && !isRegistering && authState !is AuthState.Loading) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Atau masuk dengan:",
                    color = TextGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Google Login button
                    OutlinedButton(
                        onClick = {
                            viewModel.login("sigma.google@gmail.com", "googlelogin", isFingerprint = false)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("google_login_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Google",
                            tint = Color.White,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Google Account")
                    }

                    // Fingerprint / Biometric scan button
                    OutlinedButton(
                        onClick = {
                            showBiometricDialog = true
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("fingerprint_login_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Fingerprint",
                            tint = SigmaGreen,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Fingerprint")
                    }
                }
            }
        }
    }

    // Interactive Simulated Biometric Scans Dialog
    if (showBiometricDialog) {
        AlertDialog(
            onDismissRequest = { showBiometricDialog = false },
            icon = { Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(48.dp), tint = SigmaGreen) },
            title = { Text("Autentikasi Sidik Jari") },
            text = {
                Text(
                    "Sentuhkan jari Anda pada sensor sidik jari untuk masuk secara instan ke GO SEHAT.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showBiometricDialog = false
                        viewModel.login("", "", isFingerprint = true)
                    },
                    modifier = Modifier.testTag("biometric_dialog_confirm")
                ) {
                    Text("Simulasikan Sentuh Sensor", color = SigmaGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBiometricDialog = false }) {
                    Text("Batal", color = Color.White)
                }
            },
            containerColor = CarbonCard,
            titleContentColor = Color.White,
            textContentColor = OnCarbonDark
        )
    }
}
