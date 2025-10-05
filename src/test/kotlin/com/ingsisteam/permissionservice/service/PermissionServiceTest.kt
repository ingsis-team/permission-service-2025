package com.ingsisteam.permissionservice.service

import com.ingsisteam.permissionservice.model.dto.CreatePermissionDTO
import com.ingsisteam.permissionservice.model.dto.UpdatePermissionDTO
import com.ingsisteam.permissionservice.model.enum.PermissionRole
import com.ingsisteam.permissionservice.repository.PermissionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PermissionServiceTest {
    @Autowired
    private lateinit var permissionService: PermissionService

    @Autowired
    private lateinit var permissionRepository: PermissionRepository

    @Test
    fun `should create permission successfully`() {
        val createDTO =
            CreatePermissionDTO(
                snippetId = 1L,
                userId = "user1",
                role = PermissionRole.OWNER,
            )

        val result = permissionService.createPermission(createDTO)

        assertNotNull(result)
        assertEquals(1L, result.snippetId)
        assertEquals("user1", result.userId)
        assertEquals(PermissionRole.OWNER, result.role)
    }

    @Test
    fun `should throw exception when creating duplicate permission`() {
        val createDTO =
            CreatePermissionDTO(
                snippetId = 1L,
                userId = "user1",
                role = PermissionRole.OWNER,
            )

        permissionService.createPermission(createDTO)

        assertThrows(IllegalArgumentException::class.java) {
            permissionService.createPermission(createDTO)
        }
    }

    @Test
    fun `should check permission correctly`() {
        val createDTO =
            CreatePermissionDTO(
                snippetId = 1L,
                userId = "user1",
                role = PermissionRole.READ,
            )

        permissionService.createPermission(createDTO)

        val result = permissionService.checkPermission(1L, "user1")

        assertTrue(result.hasPermission)
        assertEquals(PermissionRole.READ, result.role)
    }

    @Test
    fun `should return false for non-existent permission`() {
        val result = permissionService.checkPermission(999L, "nonexistent")

        assertFalse(result.hasPermission)
        assertNull(result.role)
    }

    @Test
    fun `should check write permission correctly`() {
        val ownerDTO =
            CreatePermissionDTO(
                snippetId = 1L,
                userId = "owner",
                role = PermissionRole.OWNER,
            )
        val writeDTO =
            CreatePermissionDTO(
                snippetId = 2L,
                userId = "writer",
                role = PermissionRole.WRITE,
            )
        val readDTO =
            CreatePermissionDTO(
                snippetId = 3L,
                userId = "reader",
                role = PermissionRole.READ,
            )

        permissionService.createPermission(ownerDTO)
        permissionService.createPermission(writeDTO)
        permissionService.createPermission(readDTO)

        assertTrue(permissionService.hasWritePermission(1L, "owner"))
        assertTrue(permissionService.hasWritePermission(2L, "writer"))
        assertFalse(permissionService.hasWritePermission(3L, "reader"))
    }

    @Test
    fun `should update permission successfully`() {
        val createDTO =
            CreatePermissionDTO(
                snippetId = 1L,
                userId = "user1",
                role = PermissionRole.READ,
            )

        permissionService.createPermission(createDTO)

        val updateDTO = UpdatePermissionDTO(role = PermissionRole.WRITE)
        val result = permissionService.updatePermission(1L, "user1", updateDTO)

        assertEquals(PermissionRole.WRITE, result.role)
    }

    @Test
    fun `should throw exception when updating non-existent permission`() {
        val updateDTO = UpdatePermissionDTO(role = PermissionRole.WRITE)

        assertThrows(NoSuchElementException::class.java) {
            permissionService.updatePermission(999L, "nonexistent", updateDTO)
        }
    }

    @Test
    fun `should delete permission successfully`() {
        val createDTO =
            CreatePermissionDTO(
                snippetId = 1L,
                userId = "user1",
                role = PermissionRole.READ,
            )

        permissionService.createPermission(createDTO)
        assertTrue(permissionRepository.existsBySnippetIdAndUserId(1L, "user1"))

        permissionService.deletePermission(1L, "user1")
        assertFalse(permissionRepository.existsBySnippetIdAndUserId(1L, "user1"))
    }

    @Test
    fun `should throw exception when deleting non-existent permission`() {
        assertThrows(NoSuchElementException::class.java) {
            permissionService.deletePermission(999L, "nonexistent")
        }
    }
}
