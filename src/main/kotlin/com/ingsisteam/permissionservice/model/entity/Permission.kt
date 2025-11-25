package com.ingsisteam.permissionservice.model.entity

import com.ingsisteam.permissionservice.model.enum.PermissionRole
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "permissions")
data class Permission(
    @Id
    @Column(length = 36)
    var id: String = "",
    @Column(name = "snippet_id", nullable = false)
    val snippetId: String,
    @Column(name = "user_id", nullable = false)
    val userId: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: PermissionRole,
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    constructor() : this(
        id = "",
        snippetId = "",
        userId = "",
        role = PermissionRole.READ,
    )

    @PrePersist
    fun generateId() {
        if (id.isEmpty()) {
            id = UUID.randomUUID().toString()
        }
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
