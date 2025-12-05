package com.example.naijadishes.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naijadishes.data.NetworkRepository
import com.example.naijadishes.model.ErrorResponse
import com.example.naijadishes.model.UserProfile
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


sealed interface UserProfileScreenUiState {
    data class Success(val response: Response<UserProfile>) : UserProfileScreenUiState
    data class Error(val message: String, val code: Int): UserProfileScreenUiState
    object Loading: UserProfileScreenUiState
}

class UserProfileScreenViewModel(
    private val networkRepository: NetworkRepository,
): ViewModel(){
    var uiState: UserProfileScreenUiState by mutableStateOf(UserProfileScreenUiState.Loading)
    var likeToggle by mutableStateOf(false)
    var username: String by mutableStateOf("")

    fun like(): Boolean{
        likeToggle = !likeToggle
        return likeToggle
    }

    fun getUserProfile(){
        viewModelScope.launch {
            uiState = UserProfileScreenUiState.Loading
            uiState = try{
                val response: Response<UserProfile> = networkRepository.getUserProfile(username)
                if (response.isSuccessful) {
                    UserProfileScreenUiState.Success(response)
                }else{
                    UserProfileScreenUiState.Error(
                        response.errorBody()?.byteString()?.utf8()?.let { jsonString ->
                            val errorResponse = Json.decodeFromString<ErrorResponse>(jsonString)
                            errorResponse.detail } ?: "Unknown Error", response.code()
                    )
                }
            }catch(_: IOException){
                UserProfileScreenUiState.Error(
                    "Network failure, please check you internet connection",
                    400
                )
            }catch (error: HttpException ){
                UserProfileScreenUiState.Error(
                    error.message ?: "Session timeout",
                    error.code()
                )
            }
        }
    }

}