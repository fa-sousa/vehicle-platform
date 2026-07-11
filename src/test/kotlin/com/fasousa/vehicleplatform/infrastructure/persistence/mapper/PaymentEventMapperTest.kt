package com.fasousa.vehicleplatform.infrastructure.persistence.mapper

import com.fasousa.vehicleplatform.domain.model.PaymentEvent
import com.fasousa.vehicleplatform.domain.model.PaymentEventStatus
import com.fasousa.vehicleplatform.infrastructure.persistence.entity.PaymentEventDocument
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PaymentEventMapperTest {

    @Test
    fun `should convert domain to document`() {
        // GIVEN
        val eventDate = LocalDateTime.now()

        val domain = PaymentEvent(
            id = "event-1",
            vehicleId = 1L,
            cpf = "12345678900",
            paymentCode = "payment-1",
            status = PaymentEventStatus.CREATED,
            eventDate = eventDate
        )

        // WHEN
        val document = PaymentEventMapper.toDocument(domain)

        // THEN
        assertEquals(domain.id, document.id)
        assertEquals(domain.vehicleId, document.vehicleId)
        assertEquals(domain.cpf, document.cpf)
        assertEquals(domain.paymentCode, document.paymentCode)
        assertEquals(domain.status, document.status)
        assertEquals(domain.eventDate, document.eventDate)
    }

    @Test
    fun `should convert document to domain`() {
        // GIVEN
        val eventDate = LocalDateTime.now()

        val document = PaymentEventDocument(
            id = "event-2",
            vehicleId = 2L,
            cpf = "98765432100",
            paymentCode = "payment-2",
            status = PaymentEventStatus.APPROVED,
            eventDate = eventDate
        )

        // WHEN
        val domain = PaymentEventMapper.toDomain(document)

        // THEN
        assertEquals(document.id, domain.id)
        assertEquals(document.vehicleId, domain.vehicleId)
        assertEquals(document.cpf, domain.cpf)
        assertEquals(document.paymentCode, domain.paymentCode)
        assertEquals(document.status, domain.status)
        assertEquals(document.eventDate, domain.eventDate)
    }
}
