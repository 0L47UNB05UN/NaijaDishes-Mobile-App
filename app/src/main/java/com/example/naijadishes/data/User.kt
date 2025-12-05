package com.example.naijadishes.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class Converters {
    @TypeConverter
    fun fromNotes(notes: List<Note>): String {
        return Json.encodeToString(notes)
    }

    @TypeConverter
    fun toNotes(value: String): List<Note> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val password: String,
    val jwt: String,
    var notes: List<Note>
)
@Serializable
data class Note(
    @PrimaryKey
    val title: String,
    val details: String
)