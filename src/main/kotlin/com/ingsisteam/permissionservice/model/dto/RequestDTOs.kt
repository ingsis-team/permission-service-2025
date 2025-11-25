package com.ingsisteam.permissionservice.model.dto

import com.ingsisteam.permissionservice.model.enum.PermissionRole
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreatePermissionDTO(
    @field:NotBlank(message = "Snippet ID cannot be null or empty")
    val snippetId: String,
    @field:NotBlank(message = "User ID cannot be null or empty")
    val userId: String,
    @field:NotNull(message = "Role cannot be null")
    val role: PermissionRole = PermissionRole.OWNER,
)

data class UpdatePermissionDTO(
    @field:NotNull(message = "Role cannot be null")
    val role: PermissionRole,
)
