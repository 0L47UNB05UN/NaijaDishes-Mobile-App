package com.example.naijadishes.data

import com.example.naijadishes.model.Category
import com.example.naijadishes.model.LoginRequest
import com.example.naijadishes.model.LoginResponse
import com.example.naijadishes.model.RegisterRequest
import com.example.naijadishes.model.RegisterResponse
import com.example.naijadishes.model.SearchResponse
import com.example.naijadishes.model.HomeScreenData
import com.example.naijadishes.model.Recipe
import com.example.naijadishes.model.UserProfile
import com.example.naijadishes.network.ApiService
import okhttp3.MultipartBody
import retrofit2.Response


interface NetworkAppRepository{
    suspend fun login( request: LoginRequest): Response<LoginResponse>
    suspend fun register( request: RegisterRequest): Response<RegisterResponse>
    suspend fun getTodaysRecipes(): Response<HomeScreenData>
    suspend fun search(query: String): Response<SearchResponse>
    suspend fun getUserProfile(username: String): Response<UserProfile>
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
    suspend fun uploadRecipe(
        name: String,
        description: String,
        ingredient: Array<String>,
        recipe: Array<String>,
        bestPaired: Array<String>,
        category: Array<Category>,
        image: MultipartBody.Part
    ): Response<Recipe> {
        return retrofitService.uploadRecipe(
            name = name, description = description, ingredient = ingredient, recipe = recipe,
            bestPaired = bestPaired, category = category, image = image
        )
    }

}