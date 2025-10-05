package com.ingsisteam.permissionservice.repository

import com.ingsisteam.permissionservice.model.entity.Permission
import com.ingsisteam.permissionservice.model.enum.PermissionRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PermissionRepository : JpaRepository<Permission, Long> {
    fun findBySnippetIdAndUserId(
        snippetId: Long,
        userId: String,
    ): Permission?

    fun findBySnippetId(snippetId: Long): List<Permission>

    fun findByUserId(userId: String): List<Permission>

    fun existsBySnippetIdAndUserId(
        snippetId: Long,
        userId: String,
    ): Boolean

    @Query("SELECT p FROM Permission p WHERE p.snippetId = :snippetId AND p.userId = :userId AND p.role IN :roles")
    fun findBySnippetIdAndUserIdAndRoleIn(
        snippetId: Long,
        userId: String,
        roles: List<PermissionRole>,
    ): Permission?

    fun deleteBySnippetIdAndUserId(
        snippetId: Long,
        userId: String,
    )
}
