package com.carlos.ismartshell.core.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    private val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale("es", "ES"))

    fun formatIsoToDisplay(isoString: String?): String {
        if (isoString == null) return "N/A"
        return try {
            val instant = Instant.parse(isoString)
            val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            dateTime.format(displayFormatter)
        } catch (e: Exception) {
            isoString
        }
    }

    fun getRemainingMillis(isoString: String?, durationMinutes: Int = 30): Long {
        if (isoString == null) return 0L
        return try {
            val instant = Instant.parse(isoString)
            val expiryTime = instant.plusSeconds(durationMinutes * 60L).toEpochMilli()
            val currentTime = Instant.now().toEpochMilli()
            (expiryTime - currentTime).coerceAtLeast(0L)
        } catch (e: Exception) {
            0L
        }
    }
}