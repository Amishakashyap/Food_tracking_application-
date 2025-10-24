package com.example.foodtracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.foodtracker.data.db.AppDatabase
import com.example.foodtracker.data.db.User
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    onRegisterSuccess: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val scope = rememberCoroutineScope()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registration", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(
            password, { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(name, { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(city, { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(gender, { gender = it }, label = { Text("Gender (Male/Female/Other)") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(age, { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(bloodGroup, { bloodGroup = it }, label = { Text("Blood Group (e.g., A+, O-)") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
        Spacer(Modifier.height(16.dp))
        
        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }
        
        Button(
            onClick = {
                val ageInt = age.toIntOrNull()
                if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && ageInt != null) {
                    isLoading = true
                    errorMsg = ""
                    scope.launch {
                        try {
                            val user = User(
                                email = email.trim(),
                                password = password,
                                name = name.trim(),
                                city = city.trim(),
                                gender = gender.trim(),
                                age = ageInt,
                                bloodGroup = bloodGroup.trim()
                            )
                            val userId = db.userDao().insert(user)
                            onRegisterSuccess(userId)
                        } catch (e: Exception) {
                            errorMsg = "Registration failed: ${e.message}"
                            isLoading = false
                        }
                    }
                } else {
                    errorMsg = "Please fill all required fields correctly"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Register")
            }
        }
        
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onNavigateBack, enabled = !isLoading) {
            Text("Already have an account? Login")
        }
    }
}
