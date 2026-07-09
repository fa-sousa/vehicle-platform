package com.fasousa.vehicleplatform.infrastructure.persistence.entity

import com.fasousa.vehicleplatform.domain.model.PaymentEventStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "payment_events")
data class PaymentEventDocument(
    @Id
    val id: String? = null,
    val vehicleId: Long,
    val cpf: String,
    val paymentCode: String?,
    val status: PaymentEventStatus,
    val eventDate: LocalDateTime
)
