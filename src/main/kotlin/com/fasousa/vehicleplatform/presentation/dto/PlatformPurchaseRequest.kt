package com.fasousa.vehicleplatform.presentation.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class PlatformPurchaseRequest(
    @field:NotBlank(message = "CPF cannot be blank")
    @field:Pattern(
        regexp = "^\\d{11}$",
        message = "CPF must contain exactly 11 digits"
    )
    val cpf: String
)
