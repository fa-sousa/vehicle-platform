package com.fasousa.vehicleplatform.infrastructure.client.dto

import java.time.LocalDateTime

data class SaleResponse(
    val id: Long?,
    val vehicleId: Long,
    val cpf: String,
    val paymentCode: String?,
    val paymentStatus: String?,
    val saleDate: LocalDateTime?
)
