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
                snippetId = "snippet1",
                userId = "user1",
                role = PermissionRole.OWNER,
            )

        val result = permissionService.createPermission(createDTO)

        assertNotNull(result)
        assertEquals("snippet1", result.snippetId)
        assertEquals("user1", result.userId)
        assertEquals(PermissionRole.OWNER, result.role)
    }

    @Test
    fun `should throw exception when creating duplicate permission`() {
        val createDTO =
            CreatePermissionDTO(
                snippetId = "snippet1",
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
                snippetId = "snippet1",
                userId = "user1",
                role = PermissionRole.READ,
            )

        permissionService.createPermission(createDTO)

        val result = permissionService.checkPermission("snippet1", "user1")

        assertTrue(result.hasPermission)
        assertEquals(PermissionRole.READ, result.role)
    }

    @Test
    fun `should return false for non-existent permission`() {
        val result = permissionService.checkPermission("nonexistent", "nonexistent")

        assertFalse(result.hasPermission)
        assertNull(result.role)
    }

    @Test
    fun `should check write permission correctly`() {
        val ownerDTO =
            CreatePermissionDTO(
                snippetId = "snippet1",
                userId = "owner",
                role = PermissionRole.OWNER,
            )
        val writeDTO =
            CreatePermissionDTO(
                snippetId = "snippet2",
                userId = "writer",
                role = PermissionRole.WRITE,
            )
        val readDTO =
            CreatePermissionDTO(
                snippetId = "snippet3",
                userId = "reader",
                role = PermissionRole.READ,
            )

        permissionService.createPermission(ownerDTO)
        permissionService.createPermission(writeDTO)
        permissionService.createPermission(readDTO)

        assertTrue(permissionService.hasWritePermission("snippet1", "owner"))
        assertTrue(permissionService.hasWritePermission("snippet2", "writer"))
        assertFalse(permissionService.hasWritePermission("snippet3", "reader"))
    }

    @Test
    fun `should update permission successfully`() {
        val createDTO =
            CreatePermissionDTO(
                snippetId = "snippet1",
                userId = "user1",
                role = PermissionRole.READ,
            )

        permissionService.createPermission(createDTO)

        val updateDTO = UpdatePermissionDTO(role = PermissionRole.WRITE)
        val result = permissionService.updatePermission("snippet1", "user1", updateDTO)

        assertEquals(PermissionRole.WRITE, result.role)
    }

    @Test
    fun `should throw exception when updating non-existent permission`() {
        val updateDTO = UpdatePermissionDTO(role = PermissionRole.WRITE)

        assertThrows(NoSuchElementException::class.java) {
            permissionService.updatePermission("nonexistent", "nonexistent", updateDTO)
        }
    }

    @Test
    fun `should delete permission successfully`() {
        val createDTO =
            CreatePermissionDTO(
                snippetId = "snippet1",
                userId = "user1",
                role = PermissionRole.READ,
            )

        permissionService.createPermission(createDTO)
        assertTrue(permissionRepository.existsBySnippetIdAndUserId("snippet1", "user1"))

        permissionService.deletePermission("snippet1", "user1")
        assertFalse(permissionRepository.existsBySnippetIdAndUserId("snippet1", "user1"))
    }

    @Test
    fun `should throw exception when deleting non-existent permission`() {
        assertThrows(NoSuchElementException::class.java) {
            permissionService.deletePermission("nonexistent", "nonexistent")
        }
    }
}
