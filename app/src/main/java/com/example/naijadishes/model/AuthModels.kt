package com.example.naijadishes.model

import android.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("username") val userName: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val message: String="",
    val jwt: String="",
)

@Serializable
data class RegisterRequest(
    @SerialName("username") val userName: String,
    val email: String,
    val password: String,
)

@Serializable
data class RegisterResponse(
    val message: String,
    @SerialName("username") val userName: String,
    val email: String
)

@Serializable
data class ErrorResponse(
    val detail: String
)

@Serializable
data class UserProfile(
    val username: String,
    val likes: Int,
    val recipes: Int
)