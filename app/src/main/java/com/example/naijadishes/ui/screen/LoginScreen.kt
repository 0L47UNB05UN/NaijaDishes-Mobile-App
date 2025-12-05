package com.example.naijadishes.ui.screen


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    appViewModel: LoginScreenViewModel
) {
    val context = LocalContext.current
    Column(
        Modifier.imePadding().fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (appViewModel.showDialog){
            Dialog(
                onDismissRequest = {}
            ) {
                    LoginProcessingScreen(
                        appViewModel.uiState,
                        appViewModel,
                        onLoginSuccess
                    )
            }
        }
        OutlinedTextField(
            value = appViewModel.email,
            onValueChange = {
                appViewModel.email = it
                appViewModel.passwordError = false
            },
            label = { Text("email") },
            isError = appViewModel.passwordError,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            value = appViewModel.password,
            onValueChange = {
                appViewModel.password = it
                appViewModel.passwordError = false
            },
            label = { Text("Password") },
            isError = appViewModel.passwordError,
            singleLine = true,
            visualTransformation = if (appViewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    processLogin(appViewModel, context)
                }
            ),
            trailingIcon = {
                val image = if (appViewModel.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (appViewModel.passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { appViewModel.passwordVisible = !appViewModel.passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )
        Button(
            onClick = {
                processLogin(appViewModel, context)
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("login")
        }
        TextButton(onClick = onRegisterClick) {
            Text("Don't have an account? Register")
        }
    }
}


fun processLogin(appViewModel: LoginScreenViewModel, context: Context){
    if (appViewModel.email.isNotBlank() and appViewModel.password.isNotBlank()) {
        appViewModel.showDialog = true
        appViewModel.login()
    }else{
        Toast.makeText(
            context,
            "Fields cannot be empty",
            Toast.LENGTH_SHORT
        ).show().also { appViewModel.passwordError = true }
    }
}
@Composable
fun LoginProcessingScreen(
    uiState: LoginScreenUiState,
    appViewModel: LoginScreenViewModel,
    onSuccess: () -> Unit,
    modifier: Modifier=Modifier
){
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {
        when (uiState) {
            is LoginScreenUiState.Success -> {
                appViewModel.showDialog = false
                onSuccess()
            }
            is LoginScreenUiState.Error -> {
                Toast.makeText(
                    context,
                    uiState.message,
                    Toast.LENGTH_SHORT
                ).show().also { appViewModel.showDialog = false  }
            }
            is LoginScreenUiState.Loading -> LoadingScreen()
        }
    }
}
