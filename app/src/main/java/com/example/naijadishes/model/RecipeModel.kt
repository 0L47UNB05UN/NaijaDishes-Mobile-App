package com.example.naijadishes.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
enum class Category(val value: String) {
    BREAKFAST("breakfast"),
    LUNCH("lunch"),
    DINNER("dinner")
}

@Serializable
data class HomeScreenData(
    val dailyRecipes : List<Recipe>,
    val breakfastRecipes: List<Recipe>,
    val lunchRecipes: List<Recipe>,
    val dinnerRecipes: List<Recipe>
){
    fun hasAnyRecipe(): Boolean{
        return dailyRecipes.isNotEmpty() || breakfastRecipes.isNotEmpty()
                || lunchRecipes.isNotEmpty() || dinnerRecipes.isNotEmpty()
    }
}
@Serializable
data class Recipe(
    val id: Int,
    val image: String,
    val name: String,
    val description: String,
    val ingredient: List<String>,
    val recipe: List<String>,
    @SerialName("best_paired")
    val bestPaired: List<String>,
    val category: List<String>,
    val author: String
)

@Serializable
data class SearchResponse(
    val recipes: List<Recipe>
)