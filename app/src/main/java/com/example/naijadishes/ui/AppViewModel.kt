package com.example.naijadishes.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.naijadishes.NaijaDishesApplication
import com.example.naijadishes.data.DefaultAppContainer
import com.example.naijadishes.ui.screen.HomeScreenViewModel
import com.example.naijadishes.ui.screen.SearchScreenViewModel
import com.example.naijadishes.ui.screen.LoginScreenViewModel
import com.example.naijadishes.ui.screen.RegisterScreenViewModel
import com.example.naijadishes.ui.screen.UserProfileScreenViewModel


object AppViewModelProvider{
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            LoginScreenViewModel(
                gNoteApplication().container.networkRepository,
                gNoteApplication().container as DefaultAppContainer
            )
        }
        initializer {
            RegisterScreenViewModel(
                gNoteApplication().container.networkRepository,
            )
        }
        initializer {
            SearchScreenViewModel(
                gNoteApplication().container.networkRepository,
            )
        }
        initializer {
            HomeScreenViewModel(
                gNoteApplication().container.networkRepository,
            )
        }
        initializer {
            UserProfileScreenViewModel(
                gNoteApplication().container.networkRepository,
            )
        }
    }
}

fun CreationExtras.gNoteApplication(): NaijaDishesApplication {
    return (this[AndroidViewModelFactory.APPLICATION_KEY] as NaijaDishesApplication)
}