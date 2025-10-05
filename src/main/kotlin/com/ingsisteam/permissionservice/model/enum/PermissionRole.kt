package com.ingsisteam.permissionservice.model.enum

enum class PermissionRole {
    OWNER, // Puede leer, escribir, compartir y eliminar
    WRITE, // Puede leer y escribir
    READ, // Solo puede leer
}
