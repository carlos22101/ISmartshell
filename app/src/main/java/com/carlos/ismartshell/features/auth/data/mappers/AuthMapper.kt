package com.carlos.ismartshell.features.auth.data.mappers

import com.carlos.ismartshell.features.auth.data.models.AuthModels
import com.carlos.ismartshell.features.auth.domain.entities.User

object AuthMapper {
    /**
     * Mapea el DTO del usuario al dominio.
     * Si los datos vienen en una estructura anidada (dto.user) se usan esos,
     * de lo contrario se intenta usar la estructura plana (dto.flatRole, etc).
     */
    fun toDomain(dto: AuthModels.UserDto?, authData: AuthModels.AuthData? = null): User {
        val finalRole = dto?.role ?: authData?.flatRole ?: "buyer"
        val finalId   = dto?.id ?: authData?.flatId ?: ""
        val finalName = dto?.name ?: "Usuario"
        val finalEmail = dto?.email ?: ""
        
        return User(
            id    = finalId,
            name  = finalName,
            email = finalEmail,
            role  = finalRole
        )
    }
}
