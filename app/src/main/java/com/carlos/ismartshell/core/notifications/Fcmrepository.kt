package com.carlos.ismartshell.core.notifications

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface FcmRepository {
    suspend fun sendTokenToBackend(token: String)
    fun getSavedToken(): String?
}

@Singleton
class FcmRepositoryImpl @Inject constructor(
    private val apiService: com.carlos.ismartshell.core.network.ApiService,
    private val tokenManager: com.carlos.ismartshell.core.local.TokenManager,
    @ApplicationContext private val context: Context
) : FcmRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)

    override suspend fun sendTokenToBackend(token: String) {
        try {
            prefs.edit().putString("fcm_token", token).apply()
            if (tokenManager.getToken() != null) {
                apiService.registerFcmToken(FcmTokenRequest(token = token))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getSavedToken(): String? = prefs.getString("fcm_token", null)
}