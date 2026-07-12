package com.fasousa.vehicleplatform.presentation.dto

data class PlatformPurchaseResponse(
    val saleId: Long?,
    val vehicleId: Long,
    val paymentCode: String?,
    val paymentStatus: String?,
    val platformEventId: String?
)
