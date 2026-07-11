package com.fasousa.vehicleplatform.presentation.controller

import com.fasousa.vehicleplatform.application.service.PlatformPurchaseService
import com.fasousa.vehicleplatform.presentation.dto.PlatformPurchaseRequest
import com.fasousa.vehicleplatform.presentation.dto.PlatformPurchaseResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class PlatformPurchaseControllerTest {

    private lateinit var platformPurchaseService: PlatformPurchaseService
    private lateinit var controller: PlatformPurchaseController

    @BeforeEach
    fun setUp() {
        platformPurchaseService =
            mock(PlatformPurchaseService::class.java)

        controller = PlatformPurchaseController(
            platformPurchaseService
        )
    }

    @Test
    fun `should delegate purchase to service`() {
        // GIVEN
        val vehicleId = 1L
        val cpf = "12345678900"

        val expectedResponse = PlatformPurchaseResponse(
            saleId = 10L,
            vehicleId = vehicleId,
            cpf = cpf,
            paymentCode = "payment-123",
            paymentStatus = "PENDING",
            platformEventId = "event-123"
        )

        `when`(
            platformPurchaseService.purchase(vehicleId, cpf)
        ).thenReturn(expectedResponse)

        // WHEN
        val response = controller.purchaseVehicle(
            vehicleId = vehicleId,
            request = PlatformPurchaseRequest(cpf = cpf)
        )

        // THEN
        assertEquals(expectedResponse, response)

        verify(platformPurchaseService)
            .purchase(vehicleId, cpf)
    }
}
