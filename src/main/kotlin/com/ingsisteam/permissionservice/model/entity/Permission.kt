package com.ingsisteam.permissionservice.model.entity

import com.ingsisteam.permissionservice.model.enum.PermissionRole
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "permissions")
data class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "snippet_id", nullable = false)
    val snippetId: Long,
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
        snippetId = 0L,
        userId = "",
        role = PermissionRole.READ,
    )

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
