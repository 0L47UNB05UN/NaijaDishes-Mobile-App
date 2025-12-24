package com.example.naijadishes.data

import com.example.naijadishes.model.HomeScreenData
import com.example.naijadishes.model.LoginRequest
import com.example.naijadishes.model.LoginResponse
import com.example.naijadishes.model.Recipe
import com.example.naijadishes.model.RegisterRequest
import com.example.naijadishes.model.RegisterResponse
import com.example.naijadishes.model.SearchResponse
import com.example.naijadishes.model.UploadRecipe
import com.example.naijadishes.model.UserProfile
import com.example.naijadishes.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File


interface NetworkAppRepository{
    suspend fun login( request: LoginRequest): Response<LoginResponse>
    suspend fun register( request: RegisterRequest): Response<RegisterResponse>
    suspend fun getTodaysRecipes(): Response<HomeScreenData>
    suspend fun search(query: String): Response<SearchResponse>
    suspend fun getUserProfile(username: String): Response<UserProfile>
    suspend fun uploadRecipe(imageFile: File, recipe: UploadRecipe): Response<Recipe>
}

class NetworkRepository(
    private val retrofitService: ApiService
): NetworkAppRepository{
    override suspend fun login( request: LoginRequest): Response<LoginResponse>{
        return retrofitService.login(request)
    }
    override suspend fun register( request: RegisterRequest): Response<RegisterResponse>{
        return retrofitService.register(request)
    }
    override suspend fun getTodaysRecipes(): Response<HomeScreenData> {
        return retrofitService.getTodaysRecipes()
    }
    override suspend fun search(query: String): Response<SearchResponse> {
        return retrofitService.search(query)
    }

    override suspend fun getUserProfile(username: String): Response<UserProfile> {
        return retrofitService.getUserProfile(username)
    }
    override suspend fun uploadRecipe(imageFile: File, recipe: UploadRecipe): Response<Recipe> {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
        return retrofitService.uploadRecipe(
            name = recipe.name.toRequestBody("text/plain".toMediaTypeOrNull()),
            description = recipe.description.toRequestBody("text/plain".toMediaTypeOrNull()),
            ingredient = recipe.ingredient.map { ingredient ->
                MultipartBody.Part.createFormData("ingredient", ingredient)
            },
            recipe = recipe.recipe.map { recipe ->
                MultipartBody.Part.createFormData("recipe", recipe)
            },
            bestPaired = recipe.bestPaired.map { bestPaired ->
                MultipartBody.Part.createFormData("best_paired", bestPaired)
            },
            category = recipe.category.map { category ->
                MultipartBody.Part.createFormData("category", category)
            },
            image = imagePart
        )
    }

}