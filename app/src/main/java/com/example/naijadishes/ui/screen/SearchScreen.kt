package com.example.naijadishes.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.naijadishes.R
import com.example.naijadishes.model.Recipe


@Composable
fun SearchScreen(
    onBackClicked: ()->Unit,
    appViewModel: SearchScreenViewModel,
    backToLogin: ()->Unit,
    onClickRecipe: (Recipe) -> Unit,
) {
    SearchScreenProcessing(
        appViewModel.uiState, appViewModel, backToLogin, onClickRecipe, onBackClicked
    )
}

@Composable
fun SearchScreenProcessing(
    uiState: SearchScreenUiState,
    appViewModel: SearchScreenViewModel,
    backToLogin: () -> Unit,
    onClickRecipe: (Recipe) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    when(uiState){
        is SearchScreenUiState.Waiting ->{
            SearchScreenRecipes(
                listOf(),
                appViewModel,
                onClickRecipe, onBackClicked
            )
        }
        is SearchScreenUiState.Success -> {
            SearchScreenRecipes(
                uiState.response.body()!!.recipes,
                appViewModel,
                onClickRecipe, onBackClicked
            )
        }
        is SearchScreenUiState.Error -> {
            Toast.makeText(
                context,
                uiState.message,
                Toast.LENGTH_SHORT
            ).show().also { appViewModel.showDialog = false  }
            when(uiState.code){
                401 -> backToLogin()
                else ->{
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = modifier.fillMaxSize()
                    ) {
                        Button(
                            onClick = appViewModel::searchRecipe
                        ) {
                            Text("retry")
                        }
                    }
                }
            }

        }
        is SearchScreenUiState.Loading -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier.fillMaxSize()
            ) {
                LoadingScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenRecipes(
    recipes: List<Recipe>,
    searchScreenViewModel: SearchScreenViewModel,
    onClickRecipe: (Recipe)->Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier= Modifier
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Search")
                        Spacer(modifier.width(16.dp))
                        val onValueChange: (String)->Unit={searchScreenViewModel.search=it}
                        OutlinedTextField(
                            value = searchScreenViewModel.search,
                            onValueChange = onValueChange,
                            singleLine = true,
                            placeholder = @Composable{
                                Text("Find recipes...") } ,
                            leadingIcon = @Composable{ Icon(
                                Icons.Filled.Search,
                                contentDescription = "Search Icon")
                            },
                            modifier = modifier
                                .weight(1f)
                                .clip(shape = RoundedCornerShape(32.dp))
                                .border(
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(32.dp),
                                    width = 2.dp
                                )
                        )
                        val context = LocalContext.current
                        FilledTonalIconButton(
                            onClick = {
                                if (searchScreenViewModel.search.isNotEmpty()) {
                                    searchScreenViewModel.searchRecipe()
                                    Log.d("mine", "called")
                                }else{
                                    Toast.makeText(
                                        context,
                                        "Input Field cannot be left empty",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "search")
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inPadding ->
        if (recipes.isNotEmpty()) {
            LazyColumn(
                contentPadding = inPadding,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment =  Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = recipes,
                    key = { recipe -> recipe.id }
                ) { recipe ->
                    Card(
                        onClick = { onClickRecipe(recipe) },
                        modifier = Modifier
                            .width(350.dp) // Wider for side-by-side
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            // Image on left
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(recipe.image)
                                    .crossfade(true)
                                    .build(),
                                error = painterResource(R.drawable.ic_connection_error),
                                placeholder = painterResource(R.drawable.loading_img),
                                contentDescription = recipe.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Text on right
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = recipe.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = recipe.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    Icons.Default.SearchOff,
                    contentDescription = "No results",
                    modifier = modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No recipes found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Try a different search",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
