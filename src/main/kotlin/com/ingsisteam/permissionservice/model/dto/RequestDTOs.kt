package com.ingsisteam.permissionservice.model.dto

import com.ingsisteam.permissionservice.model.enum.PermissionRole
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class CreatePermissionDTO(
    @field:NotNull(message = "Snippet ID cannot be null")
    @field:Positive(message = "Snippet ID must be positive")
    val snippetId: Long,
    @field:NotBlank(message = "User ID cannot be null or empty")
    val userId: String,
    @field:NotNull(message = "Role cannot be null")
    val role: PermissionRole = PermissionRole.OWNER,
)

data class UpdatePermissionDTO(
    @field:NotNull(message = "Role cannot be null")
    val role: PermissionRole,
)
