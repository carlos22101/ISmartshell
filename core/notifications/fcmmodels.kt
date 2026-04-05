package com.carlos.ismartshell.core.notifications

import com.google.gson.annotations.SerializedName

data class FcmTokenRequest(
    @SerializedName("token") val token: String
)
