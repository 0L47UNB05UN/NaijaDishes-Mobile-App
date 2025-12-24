package com.example.naijadishes.ui.screen


import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.naijadishes.model.Category
import com.example.naijadishes.model.Recipe
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import java.io.File


@Composable
fun UploadScreen(
    appViewModel: UploadScreenViewModel,
    backToLogin: () -> Unit,
) {
    Scaffold(
        topBar = {
            UploadScreenTopBar()
        }
    ) { paddingValues ->
        UploadScreenContent(
            appViewModel,
            uiState = appViewModel.uiState,
            onRetry = appViewModel::uploadRecipe,
            backToLogin = backToLogin,
            paddingValues = paddingValues
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UploadForm(
    viewModel: UploadScreenViewModel,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }

    val descriptionState = rememberRichTextState()
    val ingredientsState = rememberRichTextState()
    val recipeStepsState = rememberRichTextState()
    val bestPairedState = rememberRichTextState()
    if (!descriptionState.isOrderedList) descriptionState.toggleOrderedList()
    if (!ingredientsState.isOrderedList) ingredientsState.toggleOrderedList()
    if (!recipeStepsState.isOrderedList) recipeStepsState.toggleOrderedList()
    if (!bestPairedState.isOrderedList) bestPairedState.toggleOrderedList()

    var recipeName by remember { mutableStateOf("") }
    var selectedCategories: List<String> by remember { mutableStateOf(listOf()) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                selectedImageUri = it
                imageFile = uri.toFile(context)
            }
        }
    )

    LazyColumn(
        contentPadding = paddingValues,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Image Upload Section
            Card(
                modifier = modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    Column(
                        modifier = modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                            Text(
                            "Recipe Image",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = modifier.height(12.dp))
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray.copy(alpha = 0.3f))
                                .clickable {
                                    imagePicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Recipe image",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Add image",
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text("Tap to add image")
                                }
                            }
                        }
                    }
                }
                            }
        }

        item {
            // Recipe Name
            OutlinedTextField(
                value = recipeName,
                onValueChange = { recipeName = it },
                label = { Text("Recipe Name") },
                modifier = modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            // Description (Rich Text Editor)
            Card(
                modifier = modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = modifier.padding(16.dp)
                ) {
                    Text(
                        "Description",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = modifier.height(8.dp))
                    RichTextEditor(
                        state = descriptionState,
                        modifier = modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp),
                        colors = RichTextEditorDefaults.richTextEditorColors(
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }

        item {
            // Ingredients (Rich Text Editor)
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Ingredients",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    RichTextEditor(
                        state = ingredientsState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp),
                        placeholder = { Text("Enter ingredients (one per line)") }
                    )
                }
            }
        }
        item {
            // Recipe Steps (Rich Text Editor)
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Instructions",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    RichTextEditor(
                        state = recipeStepsState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp),
                        placeholder = { Text("Enter step-by-step instructions") }
                    )
                }
            }
        }
        item {
            // Best Paired With (Rich Text Editor)
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Best Paired With",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    RichTextEditor(
                        state = bestPairedState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp),
                        placeholder = { Text("e.g., Rice, Plantain, Salad") }
                    )
                }
            }
        }
        item {
            // Categories Selection
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Categories", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Category.entries.forEach { category ->
                            val isSelected = selectedCategories.contains(category.value)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCategories = if (isSelected) {
                                        selectedCategories - category.value
                                    } else {
                                        selectedCategories + category.value
                                    }
                                },
                                label = { Text(category.value) }
                            )
                        }
                    }
                }
            }
        }
        item {
            // Upload Button
            Button(
                onClick = {
                    viewModel.name = recipeName
                    viewModel.description = descriptionState.toText()
                    val ingredients = ingredientsState.toText()
                        .split("\n")
                        .filter { it.isNotBlank() }
                        .map { it.trim() }
                    val recipeSteps = recipeStepsState.toText()
                        .split("\n")
                        .filter { it.isNotBlank() }
                        .map { it.trim() }

                    val bestPaired = bestPairedState.toText()
                        .split("\n")
                        .filter { it.isNotBlank() }
                        .map { it.trim() }
                    viewModel.ingredient = ingredients
                    viewModel.recipe = recipeSteps
                    viewModel.bestPaired = bestPaired
                    viewModel.category = selectedCategories
                    viewModel.image = imageFile
                    viewModel.uploadRecipe()
                },
                modifier = modifier.fillMaxWidth(),
                enabled = recipeName.isNotBlank() && selectedCategories.isNotEmpty()
            ) {
                Text("Upload Recipe")
            }
        }
    }
}
@Composable
private fun UploadScreenContent(
    appViewModel: UploadScreenViewModel,
    uiState: UploadScreenUiState,
    onRetry: () -> Unit,
    backToLogin: () -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is  UploadScreenUiState.Idle -> {
            UploadForm(
                viewModel = appViewModel,
                paddingValues = paddingValues
            )
        }
        is UploadScreenUiState.Success -> {
            UploadScreenSuccess(
                recipe = uiState.response.body(),
                modifier = modifier,
                onBackToForm = {
                    appViewModel.resetForm()
                    appViewModel.uiState= UploadScreenUiState.Idle
                }
            )
        }
        is UploadScreenUiState.Error -> {
            ErrorState(
                errorMessage = uiState.message,
                errorCode = uiState.code,
                onRetry = onRetry,
                backToLogin = backToLogin,
                onBackToForm = {appViewModel.uiState = UploadScreenUiState.Idle}
            )
        }
        is UploadScreenUiState.Loading -> {
            LoadingState()
        }
    }
}

@Composable
private fun UploadScreenSuccess(
    recipe: Recipe?,
    onBackToForm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = "Success",
            modifier = Modifier.size(64.dp),
            tint = Color.Green
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Recipe Uploaded Successfully!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        recipe?.let {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
//                onClick = { onClickRecipe(it) }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    AsyncImage(
                        model = it.image,
                        contentDescription = it.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        it.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        it.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onBackToForm ) {
                Text("Upload Another")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UploadScreenTopBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.RestaurantMenu,
                    contentDescription = "Upload Recipe Screen",
                    modifier = modifier
                        .padding(end = 16.dp)
                        .scale(2f)
                )
                Text("Upload Recipe")
            }
        }
    )
}
@Composable
private fun ErrorState(
    errorMessage: String,
    errorCode: Int,
    onRetry: () -> Unit,
    backToLogin: () -> Unit,
    onBackToForm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = modifier.height(16.dp))

        Text(
            "Upload Failed",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = modifier.height(8.dp))

        Text(
            errorMessage,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Try Again")
            }

            OutlinedButton(
                onClick = onBackToForm,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Form")
            }

            if (errorCode == 401) {
                Button(
                    onClick = backToLogin,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Re-login")
                }
            }
        }
    }
}
private fun Uri.toFile(context: Context): File {
    val file = File(context.cacheDir, "recipe_${System.currentTimeMillis()}.jpg")
    context.contentResolver.openInputStream(this)?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return file
}

