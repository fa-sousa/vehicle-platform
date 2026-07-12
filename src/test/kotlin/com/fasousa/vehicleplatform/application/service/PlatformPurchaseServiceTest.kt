package com.fasousa.vehicleplatform.application.service

import com.fasousa.vehicleplatform.domain.model.PaymentEventStatus
import com.fasousa.vehicleplatform.infrastructure.client.VehicleSaleServiceClient
import com.fasousa.vehicleplatform.infrastructure.client.dto.SaleResponse
import com.fasousa.vehicleplatform.infrastructure.persistence.entity.PaymentEventDocument
import com.fasousa.vehicleplatform.infrastructure.persistence.repository.PaymentEventMongoRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.time.LocalDateTime

class PlatformPurchaseServiceTest {

    private lateinit var vehicleSaleServiceClient: VehicleSaleServiceClient
    private lateinit var paymentEventMongoRepository: PaymentEventMongoRepository
    private lateinit var service: PlatformPurchaseService

    @BeforeEach
    fun setUp() {
        vehicleSaleServiceClient = mock(VehicleSaleServiceClient::class.java)
        paymentEventMongoRepository =
            mock(PaymentEventMongoRepository::class.java)

        service = PlatformPurchaseService(
            vehicleSaleServiceClient = vehicleSaleServiceClient,
            paymentEventMongoRepository = paymentEventMongoRepository
        )
    }

    @Test
    fun `should purchase vehicle and persist payment event`() {
        // GIVEN
        val vehicleId = 10L
        val cpf = "12345678900"
        val saleDate = LocalDateTime.now()

        val saleResponse = SaleResponse(
            id = 50L,
            vehicleId = vehicleId,
            cpf = cpf,
            paymentCode = "payment-123",
            paymentStatus = "PENDING",
            saleDate = saleDate
        )

        `when`(
            vehicleSaleServiceClient.purchaseVehicle(vehicleId, cpf)
        ).thenReturn(saleResponse)

        `when`(
            paymentEventMongoRepository.save(
                org.mockito.ArgumentMatchers.any(
                    PaymentEventDocument::class.java
                )
            )
        ).thenAnswer { invocation ->
            val document =
                invocation.getArgument<PaymentEventDocument>(0)

            document.copy(id = "event-123")
        }

        // WHEN
        val response = service.purchase(vehicleId, cpf)

        // THEN
        assertEquals(50L, response.saleId)
        assertEquals(vehicleId, response.vehicleId)
        assertEquals("payment-123", response.paymentCode)
        assertEquals("PENDING", response.paymentStatus)
        assertEquals("event-123", response.platformEventId)

        verify(vehicleSaleServiceClient)
            .purchaseVehicle(vehicleId, cpf)

        verify(paymentEventMongoRepository)
            .save(
                org.mockito.ArgumentMatchers.any(
                    PaymentEventDocument::class.java
                )
            )
    }

    @Test
    fun `should save event with created status`() {
        // GIVEN
        val vehicleId = 11L
        val cpf = "98765432100"

        val saleResponse = SaleResponse(
            id = 51L,
            vehicleId = vehicleId,
            cpf = cpf,
            paymentCode = "payment-456",
            paymentStatus = "PENDING",
            saleDate = LocalDateTime.now()
        )

        `when`(
            vehicleSaleServiceClient.purchaseVehicle(vehicleId, cpf)
        ).thenReturn(saleResponse)

        `when`(
            paymentEventMongoRepository.save(
                org.mockito.ArgumentMatchers.any(
                    PaymentEventDocument::class.java
                )
            )
        ).thenAnswer { invocation ->
            invocation.getArgument<PaymentEventDocument>(0)
                .copy(id = "event-456")
        }

        val captor =
            ArgumentCaptor.forClass(PaymentEventDocument::class.java)

        // WHEN
        service.purchase(vehicleId, cpf)

        // THEN
        verify(paymentEventMongoRepository).save(captor.capture())

        val savedDocument = captor.value

        assertEquals(vehicleId, savedDocument.vehicleId)
        assertEquals(cpf, savedDocument.cpf)
        assertEquals("payment-456", savedDocument.paymentCode)
        assertEquals(PaymentEventStatus.CREATED, savedDocument.status)
        assertNotNull(savedDocument.eventDate)
    }
}
