package com.example.naijadishes.ui.screen


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.naijadishes.R
import com.example.naijadishes.model.HomeScreenData
import com.example.naijadishes.model.Recipe
import kotlinx.coroutines.delay


@Composable
fun HomeScreen(
    appViewModel: HomeScreenViewModel,
    backToLogin: () -> Unit,
    onClickRecipe: (Recipe) -> Unit,
    onCreateRecipe: () -> Unit,
    onSearchClicked: () -> Unit
) {
//    val uiState by appViewModel.uiState.collectsAsState()
    HomeScreenContent(
        appViewModel,
        uiState = appViewModel.uiState,
        onRetry = appViewModel::getTodaysRecipes,
        backToLogin = backToLogin,
        onClickRecipe = onClickRecipe,
        onCreateRecipe = onCreateRecipe,
        onSearchClicked = onSearchClicked
    )
}

@Composable
private fun HomeScreenContent(
    appViewModel: HomeScreenViewModel,
    uiState: HomeScreenUiState,
    onRetry: () -> Unit,
    backToLogin: () -> Unit,
    onClickRecipe: (Recipe) -> Unit,
    onCreateRecipe: () -> Unit,
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is HomeScreenUiState.Success -> {
            HomeScreenSuccess(
                appViewModel,
                recipes = uiState.response.body(),
                onClickRecipe = onClickRecipe,
                onCreateRecipe = onCreateRecipe,
                onSearchClicked = onSearchClicked,
                modifier = modifier
            )
        }
        is HomeScreenUiState.Error -> {
            ErrorState(
                errorMessage = uiState.message,
                errorCode = uiState.code,
                onRetry = onRetry,
                backToLogin = backToLogin
            )
        }
        is HomeScreenUiState.Loading -> {
            LoadingState()
        }
    }
}

@Composable
private fun HomeScreenSuccess(
    appViewModel: HomeScreenViewModel,
    recipes: HomeScreenData?,
    onClickRecipe: (Recipe) -> Unit,
    onCreateRecipe: () -> Unit,
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            HomeScreenTopBar(appViewModel, onSearchClicked = onSearchClicked)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateRecipe) {
                Icon(Icons.Default.Add, contentDescription = "Add Recipe")
            }
        }
    ) { paddingValues ->
        HomeScreenContent(
            recipes = recipes,
            onClickRecipe = onClickRecipe,
            modifier = modifier.padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenTopBar(
    appViewModel: HomeScreenViewModel,
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home Screen",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .scale(2f)
                )
                Text("NaijaDishes")
                Spacer(modifier = Modifier.weight(1f))
                ElevatedButton(onClick = onSearchClicked) {
                    Icon(Icons.Filled.Search, contentDescription="Search Icon")
                }
                FilledIconButton(onClick = {appViewModel.showDialog}) {
                    Icon(Icons.Default.MoreHoriz, contentDescription="More Option Icon")
                }
            }
            DropdownMenu(
                expanded = appViewModel.showDialog,
                onDismissRequest = {appViewModel.showDialog=!appViewModel.showDialog},
            ) {
                DropdownMenuItem(text = { Text("About") }, onClick = {})
                HorizontalDivider()
            }
        }
    )
}

@Composable
private fun HomeScreenContent(
    recipes: HomeScreenData?,
    onClickRecipe: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    if (recipes!!.hasAnyRecipe()) {
        HomeScreenWithRecipes(
            recipes = recipes,
            onClickRecipe = onClickRecipe,
            modifier = modifier
        )
    } else {
        EmptyState()
    }
}

@Composable
private fun HomeScreenWithRecipes(
    recipes: HomeScreenData,
    onClickRecipe: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Daily Recipes Section (Auto-scrolling)
        item {
            SectionHeader(
                title = "Daily Recipes",
                showSeeAll = false
            )
        }
        item {
            RecipeCarousel(
                recipes = recipes.dailyRecipes,
                onClickRecipe = onClickRecipe
            )
        }

        // Breakfast Section
        item {
            SectionHeader(
                title = "Breakfast Recipes",
                showSeeAll = false
            )
        }
        item {
            StaticLazyRow(
                recipes = recipes.breakfastRecipes,
                onClickRecipe = onClickRecipe
            )
        }

        // Lunch Section
        item {
            SectionHeader(
                title = "Lunch Recipes",
                showSeeAll = false
            )
        }
        item {
            StaticLazyRow(
                recipes = recipes.lunchRecipes,
                onClickRecipe = onClickRecipe
            )
        }

        // Dinner Section
        item {
            SectionHeader(
                title = "Dinner Recipes",
                showSeeAll = false
            )
        }
        item {
            StaticLazyRow(
                recipes = recipes.dinnerRecipes,
                onClickRecipe = onClickRecipe
            )
        }
        // Bottom padding
        item {
            Spacer(modifier = modifier.height(28.dp))
        }
    }
}

@Composable
private fun RecipeCarousel(
    recipes: List<Recipe>,
    onClickRecipe: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { recipes.size }
    )

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000L)
            if (!pagerState.isScrollInProgress && recipes.size > 1) {
                val nextPage = (pagerState.currentPage + 1) % recipes.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 36.dp),
            key = { recipes[it].id }
        ) { page ->
            val recipe = recipes[page]

            CarouselCard(
                recipe = recipe,
                isCurrentPage = page == pagerState.currentPage,
                onClick = { onClickRecipe(recipe) }
            )
        }

        if (recipes.size > 1) {
            CarouselIndicators(
                pageCount = recipes.size,
                currentPage = pagerState.currentPage,
                modifier = modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun CarouselCard(
    recipe: Recipe,
    isCurrentPage: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(250.dp)
            .graphicsLayer {
                scaleX = if (isCurrentPage) 1f else 0.9f
                scaleY = if (isCurrentPage) 1f else 0.9f
                alpha = if (isCurrentPage) 1f else 0.7f
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentPage) 8.dp else 4.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.image)
                    .crossfade(true)
                    .build(),
                error = painterResource(R.drawable.ic_connection_error),
                placeholder = painterResource(R.drawable.loading_img),
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = modifier.fillMaxSize()
            )
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
            )
            Column(
                modifier =modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = modifier.height(4.dp))
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CarouselIndicators(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = modifier
                    .size(if (index == currentPage) 12.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (index == currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        }
                    )
            )
        }
    }
}

@Composable
private fun StaticLazyRow(
    recipes: List<Recipe>,
    onClickRecipe: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            items = recipes,
            key = { it.id }
        ) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onClickRecipe(recipe) }
            )
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(250.dp)
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.image)
                    .crossfade(true)
                    .build(),
                error = painterResource(R.drawable.ic_connection_error),
                placeholder = painterResource(R.drawable.loading_img),
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = modifier.height(12.dp))

            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = modifier.fillMaxWidth()
            )

            Spacer(modifier = modifier.height(4.dp))

            Text(
                text = recipe.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    showSeeAll: Boolean = true,
    onClickSeeAll: () -> Unit = {},

) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = modifier.padding(end = 12.dp)
        )

        HorizontalDivider(
            modifier = modifier
                .weight(1f)
                .height(1.dp),
            color = MaterialTheme.colorScheme.outline
        )

        if (showSeeAll) {
            TextButton(
                onClick = onClickSeeAll,
                modifier = modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "More",
                    style = MaterialTheme.typography.labelSmall,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Loading recipes...")
    }
}

@Composable
private fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = "No recipes",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No recipes available",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            "Check back later or add your own!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorState(
    errorMessage: String,
    errorCode: Int,
    onRetry: () -> Unit,
    backToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LaunchedEffect(errorMessage) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

        if (errorCode == 401) {
            backToLogin()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = "Error",
            modifier = modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = modifier.height(16.dp))
        Text(
            "Something went wrong",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Try Again")
        }
    }
}
