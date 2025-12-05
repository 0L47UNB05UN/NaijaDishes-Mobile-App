package com.example.naijadishes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredentials(credentials: User)

    @Query("SELECT * FROM users LIMIT 1")
    fun getCredentials(): Flow<User?>

    @Query("DELETE FROM users")
    suspend fun clearCredentials()
//    @Query("SELECT notes from users where email= :email")
//    suspend fun getNotes( email: String): Flow<List<Note>>
//
//    @Query("SELECT notes from users where email= :email and notes = :title")
//    suspend fun getNote(email: String, title: String): Flow<Note>
}