package com.carlos.ismartshell.features.auth.data.mappers

import com.carlos.ismartshell.features.auth.data.models.UserDto
import com.carlos.ismartshell.features.auth.domain.entities.User

fun UserDto.toDomain(): User {
    return User(id = id, email = email, role = role, token = token)
}