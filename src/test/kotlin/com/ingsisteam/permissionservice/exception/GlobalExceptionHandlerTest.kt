package com.ingsisteam.permissionservice.exception

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

// DummyController moved outside for clarity and proper component scanning in webmvctest (if used) or standalone setup
@RestController
class DummyController {
    @GetMapping("/illegal-argument")
    fun throwIllegalArgument() {
        throw IllegalArgumentException("Illegal argument message")
    }

    @GetMapping("/no-such-element")
    fun throwNoSuchElement() {
        throw NoSuchElementException("No such element message")
    }

    @GetMapping("/generic-exception")
    fun throwGenericException() {
        throw Exception("Generic exception message")
    }

    @PostMapping("/validation")
    fun validation(
        @Valid @RequestBody dto: DummyDto,
    ) {
    }
}

data class DummyDto(
    @field:NotEmpty(message = "name cannot be empty")
    val name: String?,
)

class GlobalExceptionHandlerTest {
    private lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        mockMvc =
            MockMvcBuilders.standaloneSetup(DummyController())
                .setControllerAdvice(GlobalExceptionHandler())
                .build()
    }

    @Test
    fun `should handle IllegalArgumentException`() {
        mockMvc.perform(get("/illegal-argument"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Illegal argument message"))
    }

    @Test
    fun `should handle NoSuchElementException`() {
        mockMvc.perform(get("/no-such-element"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("No such element message"))
    }

    @Test
    fun `should handle generic Exception`() {
        mockMvc.perform(get("/generic-exception"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
    }

    @Test
    fun `should handle MethodArgumentNotValidException`() {
        val invalidDto = DummyDto(name = null)
        mockMvc.perform(
            post("/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)),
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.message").value("name: name cannot be empty"))
    }
}
