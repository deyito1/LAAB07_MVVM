package com.example.datossinmvvm

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch

@Composable
fun ScreenUser() {
    val context = LocalContext.current
    val db = crearDatabase(context)
    val dao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dataUser by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(50.dp))
        TextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true
        )
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name: ") },
            singleLine = true
        )
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name:") },
            singleLine = true
        )
        Button(
            onClick = {
                val user = User(0, firstName, lastName)
                coroutineScope.launch {
                    AgregarUsuario(user, dao)
                    dataUser = getUsers(dao) // Actualiza la lista después de agregar
                }
                firstName = ""
                lastName = ""
            }
        ) {
            Text("Agregar Usuario", fontSize = 16.sp)
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    dataUser = getUsers(dao)
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Listar Usuarios", fontSize = 16.sp)
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    eliminarUltimoUsuario(dao)
                    dataUser = getUsers(dao) // Actualiza la lista después de eliminar
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Eliminar Último Usuario", fontSize = 16.sp)
        }
        Text(text = dataUser, fontSize = 20.sp, modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun crearDatabase(context: Context): UserDatabase {
    return Room.databaseBuilder(
        context,
        UserDatabase::class.java,
        "user_db"
    ).build()
}

suspend fun getUsers(dao: UserDao): String {
    val users = dao.getAll()
    return users.joinToString("\n") { "${it.firstName} - ${it.lastName}" }
}

suspend fun AgregarUsuario(user: User, dao: UserDao) {
    try {
        dao.insert(user)
    } catch (e: Exception) {
        Log.e("User", "Error: insert: ${e.message}")
    }
}

suspend fun eliminarUltimoUsuario(dao: UserDao) {
    try {
        val ultimoUsuario = dao.getAll().lastOrNull() // Obtiene el último usuario
        if (ultimoUsuario != null) {
            dao.delete(ultimoUsuario)
        }
    } catch (e: Exception) {
        Log.e("User", "Error: delete: ${e.message}")
    }
}
