

**NaijaDishes** - Android Recipe Discovery App

**📱 Overview**

NaijaDishes is a modern Android application built with Jetpack Compose that allows users to discover, browse, and explore Nigerian recipes. The app features user authentication, recipe categorization, search functionality, and user profiles with a clean, intuitive UI.

**🎯 Features**

**🔐 Authentication**

* User registration with email and password validation

* Secure login with JWT token management

* Automatic session persistence using Room database

* Password visibility toggle

**🍽️ Recipe Discovery**

* Daily Recipes Carousel: Auto-scrolling featured recipes

* Categorized Recipes: Browse by breakfast, lunch, and dinner categories

* Recipe Details: Complete recipe viewing with ingredients and instructions

* Author Profiles: View recipe creators and their contributions

* Search Functionality: Find recipes by name or ingredients

**🏗️ Architecture
Tech Stack**

Kotlin - Primary programming language

Jetpack Compose - Modern declarative UI toolkit

MVVM Architecture - Separation of concerns

Room Database - Local data persistence

Retrofit - REST API communication

Coroutines - Asynchronous programming

Kotlin Serialization - JSON serialization/deserialization

**🚀 Core Components**

* **Dependency Injection Container**

    AppContainer.kt - Manages application dependencies including:

        Retrofit client with JWT authentication interceptor
        Room database instance
        Network and offline repositories

* **Navigation**

    navigation.kt - Defines app navigation graph:

        Login Screen → Home Screen
        Register Screen → Login Screen
        Home Screen → Recipe Details → User Profile
        Search Screen navigation


* **Repository Pattern**

        NetworkRepository - Handles API calls
        OfflineRepository - Manages local Room database operations

* **ViewModel Factory**

      AppViewModel.kt - Centralized ViewModel creation with proper dependency injection

**🛠️ Setup & Installation**

**Prerequisites**

Android Studio (otter)

Android SDK 24+

Kotlin 1.9+

Minimum API Level: 24

Dependencies
Key dependencies include:

androidx.compose.* - UI framework

androidx.room:* - Database

com.squareup.retrofit2:* - Networking

org.jetbrains.kotlinx:kotlinx-serialization-json - Serialization

io.coil-kt:coil-compose - Image loading

📱 Screenshots (Conceptual)

Login Screen	Home Screen	Recipe Details

Clean auth form	Recipe carousel	Full recipe view

User Profile	Search	Registration

User stats	Search results	Sign up form

**🚧 Future Enhancements**

**Planned Features**

* Recipe creation and upload

* Favorites/Bookmarks system

* Recipe rating and reviews

* Social sharing

* Offline recipe viewing

* Push notifications for new recipes

**🐛 Known Issues**

* Recipe Screen: Save functionality not fully implemented

* User Profile: Follow/Like toggle needs backend integration

* Error Handling: Some error states need better UX

* Offline Mode: Limited offline functionality

🤝 Contributing
Fork the repository

Create a feature branch (git checkout -b feature/AmazingFeature)

Commit changes (git commit -m 'Add AmazingFeature')

Push to branch (git push origin feature/AmazingFeature)

Open a Pull Request

📄 License
This project is licensed under the GNU License - see the LICENSE file for details.

**🙏 Acknowledgments**

* **Jetpack Compose** - For modern Android UI

* **Retrofit** - For seamless networking

* **Room** - For local data persistence

* **Coil** - For efficient image loading

Enjoy cooking with NaijaDishes! 🍲