package com.ingsisteam.permissionservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ingsisteam.permissionservice.model.dto.CreatePermissionDTO
import com.ingsisteam.permissionservice.model.dto.PermissionCheckResponseDTO
import com.ingsisteam.permissionservice.model.dto.PermissionResponseDTO
import com.ingsisteam.permissionservice.model.dto.UpdatePermissionDTO
import com.ingsisteam.permissionservice.model.enum.PermissionRole
import com.ingsisteam.permissionservice.service.PermissionService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(PermissionController::class)
class PermissionControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var permissionService: PermissionService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val now = LocalDateTime.now()

    @Test
    @Throws(Exception::class)
    fun `should create permission and return 201`() {
        val createDTO =
            CreatePermissionDTO(
                snippetId = "1",
                userId = "user1",
                role = PermissionRole.OWNER,
            )

        val responseDTO =
            PermissionResponseDTO(
                id = "1",
                snippetId = "1",
                userId = "user1",
                role = PermissionRole.OWNER,
                createdAt = now,
                updatedAt = now,
            )

        whenever(permissionService.createPermission(any())).thenReturn(responseDTO)

        mockMvc.perform(
            post("/api/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)),
        ).andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.snippet_id").value("1"))
            .andExpect(jsonPath("$.user_id").value("user1"))
            .andExpect(jsonPath("$.role").value("OWNER"))
    }

    @Test
    @Throws(Exception::class)
    fun `should check permission and return 200`() {
        val snippetId = "1"
        val userId = "user1"
        val response = PermissionCheckResponseDTO(hasPermission = true, role = PermissionRole.READ)

        whenever(permissionService.checkPermission(snippetId, userId)).thenReturn(response)

        mockMvc.perform(
            get("/api/permissions/check")
                .param("snippetId", snippetId.toString())
                .param("userId", userId),
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.has_permission").value(true))
            .andExpect(jsonPath("$.role").value("READ"))
    }

    @Test
    @Throws(Exception::class)
    fun `should check write permission and return 200`() {
        val snippetId = "1"
        val userId = "user1"

        whenever(permissionService.hasWritePermission(snippetId, userId)).thenReturn(true)

        mockMvc.perform(
            get("/api/permissions/write-check")
                .param("snippetId", snippetId.toString())
                .param("userId", userId),
        ).andExpect(status().isOk())
            .andExpect(content().string("true"))
    }

    @Test
    @Throws(Exception::class)
    fun `should get permissions by snippet and return 200`() {
        val snippetId = "1"
        val permissions =
            listOf(
                PermissionResponseDTO("1", snippetId, "user1", PermissionRole.OWNER, now, now),
                PermissionResponseDTO("2", snippetId, "user2", PermissionRole.READ, now, now),
            )

        whenever(permissionService.getPermissionsBySnippet(snippetId)).thenReturn(permissions)

        mockMvc.perform(get("/api/permissions/snippet/$snippetId"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].user_id").value("user1"))
            .andExpect(jsonPath("$[1].user_id").value("user2"))
    }

    @Test
    @Throws(Exception::class)
    fun `should get permissions by user and return 200`() {
        val userId = "user1"
        val permissions =
            listOf(
                PermissionResponseDTO("1", "1", userId, PermissionRole.OWNER, now, now),
                PermissionResponseDTO("2", "2", userId, PermissionRole.READ, now, now),
            )

        whenever(permissionService.getPermissionsByUser(userId)).thenReturn(permissions)

        mockMvc.perform(get("/api/permissions/user/$userId"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].snippet_id").value("1"))
            .andExpect(jsonPath("$[1].snippet_id").value("2"))
    }

    @Test
    @Throws(Exception::class)
    fun `should update permission and return 200`() {
        val snippetId = "1"
        val userId = "user1"
        val updateDTO = UpdatePermissionDTO(role = PermissionRole.WRITE)
        val responseDTO =
            PermissionResponseDTO(
                id = "1",
                snippetId = snippetId,
                userId = userId,
                role = PermissionRole.WRITE,
                createdAt = now,
                updatedAt = now,
            )

        whenever(permissionService.updatePermission(eq(snippetId), eq(userId), any())).thenReturn(responseDTO)

        mockMvc.perform(
            put("/api/permissions/snippet/$snippetId/user/$userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)),
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("WRITE"))
    }

    @Test
    @Throws(Exception::class)
    fun `should delete permission and return 204`() {
        val snippetId = "1"
        val userId = "user1"

        doNothing().whenever(permissionService).deletePermission(snippetId, userId)

        mockMvc.perform(delete("/api/permissions/snippet/$snippetId/user/$userId"))
            .andExpect(status().isNoContent())
    }
}
