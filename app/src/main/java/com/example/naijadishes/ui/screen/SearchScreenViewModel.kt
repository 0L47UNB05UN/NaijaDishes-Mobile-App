package com.example.naijadishes.ui.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naijadishes.data.NetworkRepository
import com.example.naijadishes.model.ErrorResponse
import com.example.naijadishes.model.SearchResponse
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


sealed interface SearchScreenUiState {
    data class Success(val response: Response<SearchResponse>) : SearchScreenUiState
    data class Error(val message: String, val code: Int): SearchScreenUiState
    object Loading: SearchScreenUiState
    object Waiting: SearchScreenUiState
}

class SearchScreenViewModel(
    private val networkRepository: NetworkRepository,
): ViewModel(){
    var uiState: SearchScreenUiState by mutableStateOf(SearchScreenUiState.Loading)
    var showDialog by mutableStateOf(false)
    var search by mutableStateOf("")

    init {
        uiState = SearchScreenUiState.Waiting
    }

    fun searchRecipe(){
        viewModelScope.launch{
            Log.d("mine", "viewModel called")
            uiState = SearchScreenUiState.Loading
            uiState = try{
                val response: Response<SearchResponse> = networkRepository.search(search)
                if (response.isSuccessful) {
                    SearchScreenUiState.Success(response)
                }else{
                    SearchScreenUiState.Error(
                        response.errorBody()?.byteString()?.utf8()?.let { jsonString ->
                            val errorResponse = Json.decodeFromString<ErrorResponse>(jsonString)
                            errorResponse.detail } ?: "Unknown Error", response.code()
                    )
                }
            }catch(_: IOException){
                SearchScreenUiState.Error(
                    "Network failure, please check you internet connection",
                    400
                )
            }catch (error: HttpException ){
                SearchScreenUiState.Error(
                    error.message ?: "Session timeout",
                    error.code()
                )
            }
        }
    }
}