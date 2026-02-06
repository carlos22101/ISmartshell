
package com.carlos.ismartshell.features.auth.data.mappers

import com.carlos.ismartshell.features.auth.data.models.LoginResponseDto
import com.carlos.ismartshell.features.auth.domain.entities.User


fun LoginResponseDto.toDomain(): User {
    return User(
        id = this.user.id,
        email = this.user.email,

        role = this.user.role,


        token = this.accessToken,


        username = this.user.username,
        fullName = this.user.fullName
    )
}