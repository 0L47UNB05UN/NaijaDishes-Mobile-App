package com.example.naijadishes.ui.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naijadishes.data.NetworkRepository
import com.example.naijadishes.model.Category
import com.example.naijadishes.model.ErrorResponse
import com.example.naijadishes.model.Recipe
import com.example.naijadishes.model.UploadRecipe
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.IOException


sealed interface UploadScreenUiState {
    data class Success(val response: Response<Recipe>) : UploadScreenUiState
    data class Error(val message: String, val code: Int): UploadScreenUiState
    object Loading: UploadScreenUiState
    object Idle: UploadScreenUiState
}

class UploadScreenViewModel(private val networkRepository: NetworkRepository): ViewModel(){

    var uiState: UploadScreenUiState by mutableStateOf(UploadScreenUiState.Idle)
    var image by mutableStateOf<File?>(null)
    var name: String by mutableStateOf("")
    var description: String by mutableStateOf("")
    var ingredient: List<String> by mutableStateOf(listOf())
    var recipe: List<String> by mutableStateOf(listOf())
    var bestPaired: List<String> by mutableStateOf(listOf())
    var category: List<String> by mutableStateOf(listOf())

    fun uploadRecipe(){
        viewModelScope.launch {
            uiState = UploadScreenUiState.Loading
            uiState = try{
                val response: Response<Recipe> = networkRepository.uploadRecipe(
                    image!!, UploadRecipe(
                        name = name, description = description,
                        ingredient = ingredient, recipe = recipe, bestPaired = bestPaired,
                        category = category)
                )
                if (response.isSuccessful) {
                    UploadScreenUiState.Success(response)
                }else{
                    UploadScreenUiState.Error(
                        response.errorBody()?.byteString()?.utf8()?.let { jsonString ->
                            val errorResponse = Json.decodeFromString<ErrorResponse>(jsonString)
                            errorResponse.detail } ?: "Unknown Error", response.code()
                    )
                }
            }catch(_: IOException){
                UploadScreenUiState.Error(
                    "Network failure, please check you internet connection",
                    400
                )
            }catch (error: HttpException ){
                UploadScreenUiState.Error(
                    error.message ?: "Session timeout",
                    error.code()
                )
            }
        }
    }

    fun resetForm() {
        image = null
        name = ""
        description = ""
        ingredient = emptyList()
        recipe = emptyList()
        bestPaired = emptyList()
        category = emptyList()
        uiState = UploadScreenUiState.Idle
    }
}