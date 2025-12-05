package com.example.naijadishes.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naijadishes.data.NetworkRepository
import com.example.naijadishes.model.ErrorResponse
import com.example.naijadishes.model.HomeScreenData
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


sealed interface HomeScreenUiState {
    data class Success(val response: Response<HomeScreenData>) : HomeScreenUiState
    data class Error(val message: String, val code: Int): HomeScreenUiState
    object Loading: HomeScreenUiState
}

class HomeScreenViewModel(
    private val networkRepository: NetworkRepository,
): ViewModel(){
    var uiState: HomeScreenUiState by mutableStateOf(HomeScreenUiState.Loading)
    var showDialog by mutableStateOf(false)

    init {
        getTodaysRecipes()
    }
    fun getTodaysRecipes(){
        viewModelScope.launch {
            uiState = HomeScreenUiState.Loading
            uiState = try{
                val response: Response<HomeScreenData> = networkRepository.getTodaysRecipes()
                if (response.isSuccessful) {
                    HomeScreenUiState.Success(response)
                }else{
                    HomeScreenUiState.Error(
                        response.errorBody()?.byteString()?.utf8()?.let { jsonString ->
                            val errorResponse = Json.decodeFromString<ErrorResponse>(jsonString)
                            errorResponse.detail } ?: "Unknown Error", response.code()
                    )
                }
            }catch(_: IOException){
                HomeScreenUiState.Error(
                    "Network failure, please check you internet connection",
                    400
                )
            }catch (error: HttpException ){
                HomeScreenUiState.Error(
                    error.message ?: "Session timeout",
                    error.code()
                )
            }
        }
    }

}