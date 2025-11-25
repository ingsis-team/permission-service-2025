package com.ingsisteam.permissionservice.model.dto

import com.ingsisteam.permissionservice.model.enum.PermissionRole
import java.time.LocalDateTime

data class PermissionResponseDTO(
    val id: String,
    val snippetId: String,
    val userId: String,
    val role: PermissionRole,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class PermissionCheckResponseDTO(
    val hasPermission: Boolean,
    val role: PermissionRole? = null,
)
