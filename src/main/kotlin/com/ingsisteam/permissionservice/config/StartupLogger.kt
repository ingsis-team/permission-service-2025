package com.ingsisteam.permissionservice.config

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class StartupLogger {
    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        println("\n" + "=".repeat(60))
        println("🔐 PERMISSION SERVICE IS RUNNING!")
        println("📍 Server: http://localhost:8081")
        println("📚 API Docs: http://localhost:8081/swagger-ui.html")
        println("=".repeat(60) + "\n")
    }
}
