package com.example.eval2.model

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "preferences-user")

class SettingsDataStore(private val context: Context) {
    private val MODO_TECNICO = booleanPreferencesKey("modo_tecnico")

    suspend fun guardarModoTecnico(valor: Boolean) {
        context.dataStore.edit { it[MODO_TECNICO] = valor }
    }

    fun obtenerModoTecnico(): Flow<Boolean?> =
        context.dataStore.data.map { it[MODO_TECNICO] }
}
