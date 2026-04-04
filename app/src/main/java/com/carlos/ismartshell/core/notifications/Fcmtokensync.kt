package com.carlos.ismartshell.core.notifications

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmTokenSync @Inject constructor(
    private val fcmRepository: FcmRepository
) {
    suspend fun syncToken() {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            fcmRepository.sendTokenToBackend(token)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}