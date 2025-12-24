package com.example.naijadishes.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.naijadishes.model.UserProfile

@Composable
fun UserProfileScreen(
    username: String,
    appViewModel: UserProfileScreenViewModel,
    backToLogin: () -> Unit
) {
    appViewModel.username= username
    UserProfileScreenContent(
        uiState = appViewModel.uiState,
        appViewModel = appViewModel,
        onRetry = appViewModel::getUserProfile,
        backToLogin = backToLogin
    )
}

@Composable
private fun UserProfileScreenContent(
    uiState: UserProfileScreenUiState,
    appViewModel: UserProfileScreenViewModel,
    onRetry: () -> Unit,
    backToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is UserProfileScreenUiState.Success -> {
            uiState.response.body()?.let { user ->
                UserProfileContent(user = user, appViewModel, modifier)
            } ?: run {
                ErrorState(
                    errorMessage = uiState.response.errorBody().toString(),//"I no see the User wey you want"
                    onRetry = onRetry
                )
            }
        }
        is UserProfileScreenUiState.Error -> {
            HandleErrorState(errorState = uiState, onRetry = onRetry, backToLogin = backToLogin)
        }
        is UserProfileScreenUiState.Loading -> {
            LoadingState()
            appViewModel.getUserProfile()
        }
    }
}

@Composable
private fun HandleErrorState(
    errorState: UserProfileScreenUiState.Error,
    onRetry: () -> Unit,
    backToLogin: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(errorState.message) {
        Toast.makeText(
            context,
            errorState.message,
            Toast.LENGTH_SHORT
        ).show()

        if (errorState.code == 401) {
            backToLogin()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (errorState.code != 401) {
            Icon(
                Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Failed to load profile",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}


@Composable
private fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = "Warning",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry){ Text("Retry") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileContent(
    user: UserProfile,
    appViewModel: UserProfileScreenViewModel,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            UserProfileTopBar()
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(user = user)
            Spacer(modifier = Modifier.height(32.dp))
            UserStats(user = user, appViewModel)
            Spacer(modifier = Modifier.height(32.dp))
            if (user.recipes > 0) {
                UserRecipesSection(user = user)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserProfileTopBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "User Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
private fun ProfileHeader(
    user: UserProfile,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "User Avatar",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = user.username,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun UserStats(
    user: UserProfile,
    appViewModel: UserProfileScreenViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            StatItem(
                count = user.recipes,
                label = "Recipes",
                icon = Icons.Default.RestaurantMenu,
                appViewModel
            )
            HorizontalDivider(
                modifier = Modifier
                    .height(48.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outline
            )
            StatItem(
                count = user.likes,
                label = "Likes",
                icon = Icons.Default.ThumbUp,
                toggle = true,
                appViewModel=appViewModel
            )
            HorizontalDivider(
                modifier = Modifier
                    .height(48.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outline
            )
            StatItem(
                count = user.likes,
                label = "Followers",
                icon = Icons.Default.People,
                appViewModel
            )
        }
    }
}

@Composable
private fun StatItem(
    count: Int,
    label: String,
    icon: ImageVector,
    appViewModel: UserProfileScreenViewModel,
    modifier: Modifier = Modifier,
    toggle: Boolean = false,
    onCheckChanged: (Boolean)->Unit={}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if(toggle){
            IconToggleButton(
                checked = appViewModel.likeToggle,
                onCheckedChange = onCheckChanged
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }else {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun UserRecipesSection(
    user: UserProfile,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "My Recipes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )
        // LazyRow {
        //     items(user.recipes) { recipe ->
        //         RecipeCard(recipe)
        //     }
        // }
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Restaurant,
                    contentDescription = "Recipes",
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "${user.recipes} recipes created",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { /* Navigate to user's recipes */ }) {
                    Text("View All")
                }
            }
        }
    }
}