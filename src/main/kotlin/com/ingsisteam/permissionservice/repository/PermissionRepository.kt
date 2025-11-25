package com.ingsisteam.permissionservice.repository

import com.ingsisteam.permissionservice.model.entity.Permission
import com.ingsisteam.permissionservice.model.enum.PermissionRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PermissionRepository : JpaRepository<Permission, String> {
    fun findBySnippetIdAndUserId(
        snippetId: String,
        userId: String,
    ): Permission?

    fun findBySnippetId(snippetId: String): List<Permission>

    fun findByUserId(userId: String): List<Permission>

    fun existsBySnippetIdAndUserId(
        snippetId: String,
        userId: String,
    ): Boolean

    @Query("SELECT p FROM Permission p WHERE p.snippetId = :snippetId AND p.userId = :userId AND p.role IN :roles")
    fun findBySnippetIdAndUserIdAndRoleIn(
        snippetId: String,
        userId: String,
        roles: List<PermissionRole>,
    ): Permission?

    fun deleteBySnippetIdAndUserId(
        snippetId: String,
        userId: String,
    )
}
