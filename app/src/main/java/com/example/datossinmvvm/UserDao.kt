package com.example.datossinmvvm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    suspend fun getAll(): List<User>

    @Insert
    suspend fun insert(user: User)

    // Agregar la anotación @Delete y marcarlo como suspend
    @Delete
    suspend fun delete(ultimoUsuario: User)
}
