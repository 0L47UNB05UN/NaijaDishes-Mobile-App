package com.example.naijadishes.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naijadishes.data.DefaultAppContainer
import com.example.naijadishes.data.NetworkRepository
import com.example.naijadishes.data.User
import com.example.naijadishes.model.ErrorResponse
import com.example.naijadishes.model.LoginRequest
import com.example.naijadishes.model.LoginResponse
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlinx.serialization.json.Json



sealed interface LoginScreenUiState {
    data class Success(val response: Response<LoginResponse>) : LoginScreenUiState
    data class Error(val message: String): LoginScreenUiState
    object Loading: LoginScreenUiState
}

class LoginScreenViewModel(
    private val networkRepository: NetworkRepository,
    private val container: DefaultAppContainer
): ViewModel(){
    val user: StateFlow<User> = container.offlineRepository.getCredentials()
        .filterNotNull()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            User("", "", "", listOf())
        )
    var uiState: LoginScreenUiState by mutableStateOf(LoginScreenUiState.Loading)
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordError by  mutableStateOf(false)
    var showDialog by mutableStateOf(false)
    var passwordVisible by mutableStateOf(false)

//    init {
//        oneTimeLogin()
//    }

    fun oneTimeLogin(){
        viewModelScope.launch {
            email = user.value.email
            password = user.value.password
            container.setToken(user.value.jwt)

            uiState = try{
                val response: Response<LoginResponse> = networkRepository.login(
                    LoginRequest(email, password)
                )
                passwordError = !response.message().contains("timeout")
                if (response.isSuccessful) {
                    container.offlineRepository.insertCredentials(
                        User( email, password,
                            response.body()?.jwt ?: user.value.jwt, user.value.notes
                        )
                    )
                    LoginScreenUiState.Success(response)
                }
                else{
                    LoginScreenUiState.Error(
                        response.errorBody()?.byteString()?.utf8()?.let { jsonString ->
                            val errorResponse = Json.decodeFromString<ErrorResponse>(jsonString)
                            errorResponse.detail
                        } ?: "Unknown Error"
                    )
                }
            }catch(error: IOException){
                LoginScreenUiState.Error(error.message ?: "IO Error")
            }catch (error: HttpException ){
                LoginScreenUiState.Error(error.message ?: "Network Error")
            }
        }
    }

    fun login(){
        viewModelScope.launch {
            uiState = LoginScreenUiState.Loading
            uiState = try{
                val response: Response<LoginResponse> = networkRepository.login(
                    LoginRequest(email, password)
                )
                passwordError = !response.message().contains("timeout")
                if (response.isSuccessful) {
                    container.setToken(response.body()?.jwt ?: "")
                    LoginScreenUiState.Success(response)
                    }
                else{
//                    Log.d("mine", response.errorBody()?.byteString()?.utf8() )
                    LoginScreenUiState.Error(
                        response.errorBody()?.byteString()?.utf8()?.let { jsonString ->
                            val errorResponse = Json.decodeFromString<ErrorResponse>(jsonString)
                            errorResponse.detail
                        } ?: "Unknown Error"
                    )
                }
            }catch(error: IOException){
                LoginScreenUiState.Error(error.message ?: "IO Error")
            }catch (error: HttpException ){
                LoginScreenUiState.Error(error.message ?: "Network Error")
            }
        }
    }
}