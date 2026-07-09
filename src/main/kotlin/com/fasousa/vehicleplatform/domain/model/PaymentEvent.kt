package com.fasousa.vehicleplatform.domain.model

import java.time.LocalDateTime

data class PaymentEvent(
    val id: String? = null,
    val vehicleId: Long,
    val cpf: String,
    val paymentCode: String? = null,
    val status: PaymentEventStatus,
    val eventDate: LocalDateTime = LocalDateTime.now()
)
