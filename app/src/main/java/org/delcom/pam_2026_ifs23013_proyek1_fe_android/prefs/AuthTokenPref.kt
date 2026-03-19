package org.delcom.pam_2026_ifs23013_proyek1_fe_android.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Membuat instance DataStore (Hanya 1 instance untuk seluruh aplikasi)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_token_prefs")

class AuthTokenPref(private val context: Context) {

    companion object {
        // Mendefinisikan Key/Kunci dengan tipe StringPreferencesKey
        private val AUTH_TOKEN_KEY = stringPreferencesKey("AUTH_TOKEN_KEY")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("REFRESH_TOKEN_KEY")
    }

    // ================== AUTH TOKEN ==================

    // Menyimpan token (Kini menggunakan suspend function)
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    // Mengambil token (Mengembalikan Flow agar bisa diamati / dibaca secara asinkron)
    fun getAuthToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }
    }

    // Menghapus token
    suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
        }
    }

    // ================== REFRESH TOKEN ==================

    // Menyimpan refresh token
    suspend fun saveRefreshToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = token
        }
    }

    // Mengambil refresh token
    fun getRefreshToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }
    }

    // Menghapus refresh token
    suspend fun clearRefreshToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }
}