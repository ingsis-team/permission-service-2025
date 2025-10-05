package com.ingsisteam.permissionservice.service

import com.ingsisteam.permissionservice.model.dto.CreatePermissionDTO
import com.ingsisteam.permissionservice.model.dto.PermissionCheckResponseDTO
import com.ingsisteam.permissionservice.model.dto.PermissionResponseDTO
import com.ingsisteam.permissionservice.model.dto.UpdatePermissionDTO
import com.ingsisteam.permissionservice.model.entity.Permission
import com.ingsisteam.permissionservice.model.enum.PermissionRole
import com.ingsisteam.permissionservice.repository.PermissionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PermissionService(
    private val permissionRepository: PermissionRepository,
) {
    fun createPermission(createPermissionDTO: CreatePermissionDTO): PermissionResponseDTO {
        // Verificar si ya existe un permiso para esta combinación
        val existingPermission =
            permissionRepository.findBySnippetIdAndUserId(
                createPermissionDTO.snippetId,
                createPermissionDTO.userId,
            )

        if (existingPermission != null) {
            throw IllegalArgumentException(
                "Permission already exists for snippet ${createPermissionDTO.snippetId} " +
                    "and user ${createPermissionDTO.userId}",
            )
        }

        val permission =
            Permission(
                snippetId = createPermissionDTO.snippetId,
                userId = createPermissionDTO.userId,
                role = createPermissionDTO.role,
            )

        val savedPermission = permissionRepository.save(permission)
        return toResponseDTO(savedPermission)
    }

    @Transactional(readOnly = true)
    fun checkPermission(
        snippetId: Long,
        userId: String,
    ): PermissionCheckResponseDTO {
        val permission = permissionRepository.findBySnippetIdAndUserId(snippetId, userId)
        return PermissionCheckResponseDTO(
            hasPermission = permission != null,
            role = permission?.role,
        )
    }

    @Transactional(readOnly = true)
    fun hasWritePermission(
        snippetId: Long,
        userId: String,
    ): Boolean {
        val permission =
            permissionRepository.findBySnippetIdAndUserIdAndRoleIn(
                snippetId,
                userId,
                listOf(PermissionRole.OWNER, PermissionRole.WRITE),
            )
        return permission != null
    }

    @Transactional(readOnly = true)
    fun getPermissionsBySnippet(snippetId: Long): List<PermissionResponseDTO> {
        return permissionRepository.findBySnippetId(snippetId)
            .map { toResponseDTO(it) }
    }

    @Transactional(readOnly = true)
    fun getPermissionsByUser(userId: String): List<PermissionResponseDTO> {
        return permissionRepository.findByUserId(userId)
            .map { toResponseDTO(it) }
    }

    fun updatePermission(
        snippetId: Long,
        userId: String,
        updatePermissionDTO: UpdatePermissionDTO,
    ): PermissionResponseDTO {
        val permission =
            permissionRepository.findBySnippetIdAndUserId(snippetId, userId)
                ?: throw NoSuchElementException("Permission not found for snippet $snippetId and user $userId")

        permission.role = updatePermissionDTO.role
        val updatedPermission = permissionRepository.save(permission)
        return toResponseDTO(updatedPermission)
    }

    fun deletePermission(
        snippetId: Long,
        userId: String,
    ) {
        if (!permissionRepository.existsBySnippetIdAndUserId(snippetId, userId)) {
            throw NoSuchElementException("Permission not found for snippet $snippetId and user $userId")
        }
        permissionRepository.deleteBySnippetIdAndUserId(snippetId, userId)
    }

    private fun toResponseDTO(permission: Permission): PermissionResponseDTO {
        return PermissionResponseDTO(
            id = permission.id,
            snippetId = permission.snippetId,
            userId = permission.userId,
            role = permission.role,
            createdAt = permission.createdAt,
            updatedAt = permission.updatedAt,
        )
    }
}
