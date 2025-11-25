package com.ingsisteam.permissionservice.service

import com.ingsisteam.permissionservice.model.dto.CreatePermissionDTO
import com.ingsisteam.permissionservice.model.dto.PermissionCheckResponseDTO
import com.ingsisteam.permissionservice.model.dto.PermissionResponseDTO
import com.ingsisteam.permissionservice.model.dto.UpdatePermissionDTO
import com.ingsisteam.permissionservice.model.entity.Permission
import com.ingsisteam.permissionservice.model.enum.PermissionRole
import com.ingsisteam.permissionservice.repository.PermissionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PermissionService(
    private val permissionRepository: PermissionRepository,
) {
    private val logger = LoggerFactory.getLogger(PermissionService::class.java)

    fun createPermission(createPermissionDTO: CreatePermissionDTO): PermissionResponseDTO {
        logger.debug(
            "Verificando si existe permiso para snippet {} y usuario {}",
            createPermissionDTO.snippetId,
            createPermissionDTO.userId,
        )
        val existingPermission =
            permissionRepository.findBySnippetIdAndUserId(
                createPermissionDTO.snippetId,
                createPermissionDTO.userId,
            )

        if (existingPermission != null) {
            logger.warn(
                "Intento de crear permiso duplicado para snippet {} y usuario {}",
                createPermissionDTO.snippetId,
                createPermissionDTO.userId,
            )
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
        logger.info(
            "Permiso creado: ID {}, Snippet {}, Usuario {}, Rol {}",
            savedPermission.id,
            savedPermission.snippetId,
            savedPermission.userId,
            savedPermission.role,
        )
        return toResponseDTO(savedPermission)
    }

    @Transactional(readOnly = true)
    fun checkPermission(
        snippetId: String,
        userId: String,
    ): PermissionCheckResponseDTO {
        val permission = permissionRepository.findBySnippetIdAndUserId(snippetId, userId)
        val result =
            PermissionCheckResponseDTO(
                hasPermission = permission != null,
                role = permission?.role,
            )
        logger.debug(
            "Verificación de permiso: Snippet {}, Usuario {}, Tiene permiso: {}, Rol: {}",
            snippetId,
            userId,
            result.hasPermission,
            result.role,
        )
        return result
    }

    @Transactional(readOnly = true)
    fun hasWritePermission(
        snippetId: String,
        userId: String,
    ): Boolean {
        val permission =
            permissionRepository.findBySnippetIdAndUserIdAndRoleIn(
                snippetId,
                userId,
                listOf(PermissionRole.OWNER, PermissionRole.WRITE),
            )
        val hasPermission = permission != null
        logger.debug(
            "Verificación de permiso de escritura: Snippet {}, Usuario {}, Resultado: {}",
            snippetId,
            userId,
            hasPermission,
        )
        return hasPermission
    }

    @Transactional(readOnly = true)
    fun getPermissionsBySnippet(snippetId: String): List<PermissionResponseDTO> {
        val permissions = permissionRepository.findBySnippetId(snippetId)
        logger.debug("Obtenidos {} permisos para snippet {}", permissions.size, snippetId)
        return permissions.map { toResponseDTO(it) }
    }

    @Transactional(readOnly = true)
    fun getPermissionsByUser(userId: String): List<PermissionResponseDTO> {
        val permissions = permissionRepository.findByUserId(userId)
        logger.debug("Obtenidos {} permisos para usuario {}", permissions.size, userId)
        return permissions.map { toResponseDTO(it) }
    }

    fun updatePermission(
        snippetId: String,
        userId: String,
        updatePermissionDTO: UpdatePermissionDTO,
    ): PermissionResponseDTO {
        val permission =
            permissionRepository.findBySnippetIdAndUserId(snippetId, userId)
                ?: run {
                    logger.warn(
                        "Intento de actualizar permiso inexistente para snippet {} y usuario {}",
                        snippetId,
                        userId,
                    )
                    throw NoSuchElementException(
                        "Permission not found for snippet $snippetId and user $userId",
                    )
                }

        val oldRole = permission.role
        permission.role = updatePermissionDTO.role
        val updatedPermission = permissionRepository.save(permission)
        logger.info(
            "Permiso actualizado: Snippet {}, Usuario {}, Rol anterior: {}, Rol nuevo: {}",
            snippetId,
            userId,
            oldRole,
            updatePermissionDTO.role,
        )
        return toResponseDTO(updatedPermission)
    }

    fun deletePermission(
        snippetId: String,
        userId: String,
    ) {
        if (!permissionRepository.existsBySnippetIdAndUserId(snippetId, userId)) {
            logger.warn("Intento de eliminar permiso inexistente para snippet {} y usuario {}", snippetId, userId)
            throw NoSuchElementException("Permission not found for snippet $snippetId and user $userId")
        }
        permissionRepository.deleteBySnippetIdAndUserId(snippetId, userId)
        logger.info("Permiso eliminado para snippet {} y usuario {}", snippetId, userId)
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
