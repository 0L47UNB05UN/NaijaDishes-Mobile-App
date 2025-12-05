package com.example.naijadishes.data

import kotlinx.coroutines.flow.Flow

interface AppOfflineRepository {
    fun getCredentials(): Flow<User?>
    suspend fun insertCredentials(user: User)
    suspend fun clearCredentials()
}
class OfflineRepository(
    private val database: UserDao
): AppOfflineRepository{
    override fun getCredentials(): Flow<User?> {
        return database.getCredentials()
    }
    override suspend fun insertCredentials(user: User) {
        database.insertCredentials(user)
    }
    override suspend fun clearCredentials() {
        database.clearCredentials()
    }

}