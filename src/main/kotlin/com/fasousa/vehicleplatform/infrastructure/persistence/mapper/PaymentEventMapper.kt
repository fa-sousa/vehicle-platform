package com.fasousa.vehicleplatform.infrastructure.persistence.mapper

import com.fasousa.vehicleplatform.domain.model.PaymentEvent
import com.fasousa.vehicleplatform.infrastructure.persistence.entity.PaymentEventDocument

object PaymentEventMapper {

    fun toDocument(domain: PaymentEvent): PaymentEventDocument {
        return PaymentEventDocument(
            id = domain.id,
            vehicleId = domain.vehicleId,
            cpf = domain.cpf,
            paymentCode = domain.paymentCode,
            status = domain.status,
            eventDate = domain.eventDate
        )
    }

    fun toDomain(document: PaymentEventDocument): PaymentEvent {
        return PaymentEvent(
            id = document.id,
            vehicleId = document.vehicleId,
            cpf = document.cpf,
            paymentCode = document.paymentCode,
            status = document.status,
            eventDate = document.eventDate
        )
    }
}
