package com.carlos.ismartshell.core.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_TOKEN = stringPreferencesKey("jwt_token")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_ROLE = stringPreferencesKey("user_role")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
    }

    suspend fun saveSession(token: String?, userId: String?, role: String?, name: String?) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN]     = token ?: ""
            prefs[KEY_USER_ID]   = userId ?: ""
            prefs[KEY_USER_ROLE] = role ?: ""
            prefs[KEY_USER_NAME] = name ?: ""
        }
    }

    suspend fun getToken(): String? =
        context.dataStore.data.map { it[KEY_TOKEN] }.firstOrNull()

    suspend fun getUserId(): String? =
        context.dataStore.data.map { it[KEY_USER_ID] }.firstOrNull()

    suspend fun getUserRole(): String? =
        context.dataStore.data.map { it[KEY_USER_ROLE] }.firstOrNull()

    val userRoleFlow: Flow<String?> =
        context.dataStore.data.map { it[KEY_USER_ROLE] }

    val isLoggedInFlow: Flow<Boolean> =
        context.dataStore.data.map { it[KEY_TOKEN] }.map { !it.isNullOrBlank() }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
