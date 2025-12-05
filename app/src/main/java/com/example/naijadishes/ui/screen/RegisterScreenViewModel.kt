package com.example.naijadishes.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naijadishes.data.NetworkRepository
import com.example.naijadishes.model.ErrorResponse
import com.example.naijadishes.model.RegisterRequest
import com.example.naijadishes.model.RegisterResponse
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


sealed interface RegisterScreenUiState {
    data class Success(val response: Response<RegisterResponse>) : RegisterScreenUiState
    data class Error(val message: String): RegisterScreenUiState
    object Loading: RegisterScreenUiState
}

class RegisterScreenViewModel(
    private val networkRepository: NetworkRepository,
): ViewModel(){
    var uiState: RegisterScreenUiState by mutableStateOf(RegisterScreenUiState.Loading)
    var userName by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmedPassword by  mutableStateOf("")
    var passwordError by  mutableStateOf(false)
    var showDialog by mutableStateOf(false)
    var dataValid by mutableStateOf(false)
    var passwordVisible by mutableStateOf(false)

    fun register(){
        viewModelScope.launch {
            uiState = RegisterScreenUiState.Loading
            uiState = try{
                val response: Response<RegisterResponse> = networkRepository.register(
                    RegisterRequest( userName, email, password )
                )
                if (response.isSuccessful) {
                    RegisterScreenUiState.Success(response)
                }else{
                    if (response.code() == 500)dataValid = true
                    RegisterScreenUiState.Error(
                        response.errorBody()?.byteString()?.utf8()?.let { jsonString ->
                            val errorResponse = Json.decodeFromString<ErrorResponse>(jsonString)
                            errorResponse.detail
                        } ?: "Unknown Error"
                    )
                }
            }catch(error: IOException){
                RegisterScreenUiState.Error(error.message ?: "IO Error")
            }catch (error: HttpException ){
                RegisterScreenUiState.Error(error.message ?: "Network Error")
            }
        }
    }
    fun resetViewModel(){
        uiState = RegisterScreenUiState.Loading
        userName = ""
        email = ""
        password = ""
        confirmedPassword= ""
        password = ""
    }

}