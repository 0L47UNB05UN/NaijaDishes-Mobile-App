package com.example.naijadishes.network


import com.example.naijadishes.model.Category
import com.example.naijadishes.model.HomeScreenData
import com.example.naijadishes.model.LoginRequest
import com.example.naijadishes.model.LoginResponse
import com.example.naijadishes.model.Recipe
import com.example.naijadishes.model.RegisterRequest
import com.example.naijadishes.model.RegisterResponse
import com.example.naijadishes.model.SearchResponse
import com.example.naijadishes.model.UserProfile
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ApiService {
    @POST("user/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    @GET("recipe/todayrecipes")
    suspend fun getTodaysRecipes(): Response<HomeScreenData>
    @GET("recipe/search")
    suspend fun search(@Query("query") query: String): Response<SearchResponse>
    @GET("recipe/recipe_category")
    suspend fun recipeCategory(@Query("cat") category: String): Response<List<Recipe>>
    @POST("user/user_profile")
    suspend fun getUserProfile(@Body username: String): Response<UserProfile>
    @Multipart
    @POST("upload_recipe")
    suspend fun uploadRecipe(
        @Part("name") name: String,
        @Part("description") description: String,
        @Part("ingredient") ingredient: Array<String>,
        @Part("recipe") recipe: Array<String>,
        @Part("best_paired") bestPaired: Array<String>,
        @Part("category") category: Array<Category>,
        @Part image: MultipartBody.Part ): Response<Recipe>
}
