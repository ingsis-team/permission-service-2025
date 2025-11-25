package com.ingsisteam.permissionservice.controller

import com.ingsisteam.permissionservice.model.dto.CreatePermissionDTO
import com.ingsisteam.permissionservice.model.dto.PermissionCheckResponseDTO
import com.ingsisteam.permissionservice.model.dto.PermissionResponseDTO
import com.ingsisteam.permissionservice.model.dto.UpdatePermissionDTO
import com.ingsisteam.permissionservice.service.PermissionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permission Controller", description = "API para gestionar permisos de snippets")
class PermissionController(
    private val permissionService: PermissionService,
) {
    private val logger = LoggerFactory.getLogger(PermissionController::class.java)

    @PostMapping
    @Operation(summary = "Crear permiso", description = "Crea un nuevo permiso para un snippet y usuario")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Permiso creado exitosamente"),
            ApiResponse(responseCode = "400", description = "Datos inválidos o permiso duplicado"),
        ],
    )
    fun createPermission(
        @Valid @RequestBody createPermissionDTO: CreatePermissionDTO,
    ): ResponseEntity<PermissionResponseDTO> {
        logger.info(
            "Creando permiso para snippet {} y usuario {}",
            createPermissionDTO.snippetId,
            createPermissionDTO.userId,
        )
        val permission = permissionService.createPermission(createPermissionDTO)
        logger.info("Permiso creado exitosamente con ID {}", permission.id)
        return ResponseEntity.status(HttpStatus.CREATED).body(permission)
    }

    @GetMapping("/check")
    @Operation(summary = "Verificar permiso", description = "Verifica si un usuario tiene permisos sobre un snippet")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Verificación completada"),
        ],
    )
    fun checkPermission(
        @Parameter(description = "ID del snippet") @RequestParam snippetId: String,
        @Parameter(description = "ID del usuario") @RequestParam userId: String,
    ): ResponseEntity<PermissionCheckResponseDTO> {
        logger.debug("Verificando permiso para snippet {} y usuario {}", snippetId, userId)
        val result = permissionService.checkPermission(snippetId, userId)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/write-check")
    @Operation(
        summary = "Verificar permiso de escritura",
        description = "Verifica si un usuario puede escribir en un snippet",
    )
    fun checkWritePermission(
        @RequestParam snippetId: String,
        @RequestParam userId: String,
    ): ResponseEntity<Boolean> {
        logger.debug("Verificando permiso de escritura para snippet {} y usuario {}", snippetId, userId)
        val hasWritePermission = permissionService.hasWritePermission(snippetId, userId)
        return ResponseEntity.ok(hasWritePermission)
    }

    @GetMapping("/snippet/{snippetId}")
    @Operation(summary = "Obtener permisos por snippet", description = "Obtiene todos los permisos de un snippet")
    fun getPermissionsBySnippet(
        @PathVariable snippetId: String,
    ): ResponseEntity<List<PermissionResponseDTO>> {
        logger.debug("Obteniendo permisos para snippet {}", snippetId)
        val permissions = permissionService.getPermissionsBySnippet(snippetId)
        return ResponseEntity.ok(permissions)
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener permisos por usuario", description = "Obtiene todos los permisos de un usuario")
    fun getPermissionsByUser(
        @PathVariable userId: String,
    ): ResponseEntity<List<PermissionResponseDTO>> {
        logger.debug("Obteniendo permisos para usuario {}", userId)
        val permissions = permissionService.getPermissionsByUser(userId)
        return ResponseEntity.ok(permissions)
    }

    @PutMapping("/snippet/{snippetId}/user/{userId}")
    @Operation(summary = "Actualizar permiso", description = "Actualiza el rol de un permiso existente")
    fun updatePermission(
        @PathVariable snippetId: String,
        @PathVariable userId: String,
        @Valid @RequestBody updatePermissionDTO: UpdatePermissionDTO,
    ): ResponseEntity<PermissionResponseDTO> {
        logger.info(
            "Actualizando permiso para snippet {} y usuario {} a rol {}",
            snippetId,
            userId,
            updatePermissionDTO.role,
        )
        val permission = permissionService.updatePermission(snippetId, userId, updatePermissionDTO)
        logger.info("Permiso actualizado exitosamente")
        return ResponseEntity.ok(permission)
    }

    @DeleteMapping("/snippet/{snippetId}/user/{userId}")
    @Operation(summary = "Eliminar permiso", description = "Elimina un permiso existente")
    fun deletePermission(
        @PathVariable snippetId: String,
        @PathVariable userId: String,
    ): ResponseEntity<Unit> {
        logger.info("Eliminando permiso para snippet {} y usuario {}", snippetId, userId)
        permissionService.deletePermission(snippetId, userId)
        logger.info("Permiso eliminado exitosamente")
        return ResponseEntity.noContent().build()
    }
}
