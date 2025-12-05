package com.example.naijadishes.ui.screen


import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.naijadishes.R


@Composable
fun RegisterScreen(
    onRegisterSuccessful: () -> Unit,
    appViewModel: RegisterScreenViewModel
) {
    val context = LocalContext.current
    var emptyFields by remember { mutableStateOf(false) }
    if (appViewModel.showDialog){
        RegisterProcessingScreen(
            appViewModel.uiState,
            appViewModel,
            onRegisterSuccessful
        )
    }
    Column(
        Modifier.imePadding().fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = appViewModel.userName,
            onValueChange = { appViewModel.userName = it; emptyFields=false; appViewModel.passwordError=false },
            label = { Text("Username ") },
            isError = (appViewModel.userName.isEmpty() and emptyFields) or appViewModel.dataValid,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            value = appViewModel.email,
            onValueChange = { appViewModel.email = it; emptyFields=false; appViewModel.passwordError=false },
            label = { Text("email") },
            isError = (appViewModel.email.isEmpty() and emptyFields) or appViewModel.dataValid,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            value = appViewModel.password,
            onValueChange = { appViewModel.password = it; appViewModel.passwordError=false },
            label = { Text("password") },
            isError = appViewModel.password.isEmpty() and appViewModel.passwordError,
            singleLine = true,
            visualTransformation = if (appViewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                val image = if (appViewModel.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (appViewModel.passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { appViewModel.passwordVisible = !appViewModel.passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )
        OutlinedTextField(
            value = appViewModel.confirmedPassword,
            onValueChange = { appViewModel.confirmedPassword = it; appViewModel.passwordError=false },
            label = { Text("re-enter password") },
            isError = appViewModel.confirmedPassword.isEmpty() and appViewModel.passwordError,
            singleLine = true,
            visualTransformation = if (appViewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    processRegistration(appViewModel, context, {emptyFields=it})
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
                processRegistration(appViewModel, context, {emptyFields=it})
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Register")
        }
    }
}

fun processRegistration(
    appViewModel: RegisterScreenViewModel,
    context: Context, emptyFields: (Boolean)-> Unit){
    if (appViewModel.userName.isNotEmpty() and appViewModel.email.isNotEmpty() and appViewModel.password.isNotEmpty()){
        if (appViewModel.password == appViewModel.confirmedPassword) {
            appViewModel.register()
            appViewModel.showDialog = true
        } else {
            appViewModel.passwordError = true
            Toast.makeText(
                context,
                "Passwords don't match",
                Toast.LENGTH_SHORT
            ).show()
        }
    }else{
        Toast.makeText(
            context,
            "Fields cannot be left empty",
            Toast.LENGTH_SHORT
        ).show().also {
            emptyFields(true)
            appViewModel.passwordError = true
        }
    }
}
@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
){
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing)
        )
    )
    Image(
        painter = painterResource(R.drawable.loading_img),
        contentDescription = "Loading Image",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(
                rotationZ = rotation
            )
    )
}

@Composable
fun RegisterProcessingScreen(
    uiState: RegisterScreenUiState,
    appViewModel: RegisterScreenViewModel,
    onRegisterSuccessful: () -> Unit,
    modifier: Modifier=Modifier
){
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (uiState) {
            is RegisterScreenUiState.Success -> {
                Toast.makeText(
                    context,
                    uiState.response.body()?.message,
                    Toast.LENGTH_LONG
                ).show().also {
                    appViewModel.showDialog = false
                    onRegisterSuccessful()
                    appViewModel.resetViewModel()
                }
            }
            is RegisterScreenUiState.Error -> {
                Toast.makeText(
                    context,
                    uiState.message,
                    Toast.LENGTH_SHORT
                ).show().also { appViewModel.showDialog = false  }
            }
            is RegisterScreenUiState.Loading -> LoadingScreen()
        }
    }
}

