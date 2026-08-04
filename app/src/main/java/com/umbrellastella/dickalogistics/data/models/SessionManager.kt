package com.umbrellastella.dickalogistics.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para inicializar DataStore
val Context.dataStore by preferencesDataStore(name = "dicka_sesion")

class SessionManager(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
        val ROL_KEY = stringPreferencesKey("user_rol")
    }

    // Guardar el token y rol cuando el Login sea exitoso
    suspend fun guardarSesion(token: String, rol: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[ROL_KEY] = rol
        }
    }

    // Leer el token en tiempo real (Flow)
    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY]
        }

    // Borrar sesión (por si el usuario decide cerrar sesión manualmente)
    suspend fun cerrarSesion() {
        context.dataStore.edit { preferences ->
            preferences.clear() // 🧹 Borra el token, el rol y cualquier dato de sesión guardado
        }
    }


}