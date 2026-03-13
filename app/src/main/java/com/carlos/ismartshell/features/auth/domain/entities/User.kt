package com.carlos.ismartshell.features.auth.domain.entities

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String        // "seller" | "buyer"
) {
    val isSeller get() = role == "seller"
    val isBuyer  get() = role == "buyer"
}