package com.fasousa.vehicleplatform.infrastructure.client.dto

import com.fasousa.vehicleplatform.infrastructure.client.exception.SaleServiceException
import com.fasousa.vehicleplatform.infrastructure.client.exception.VehicleNotFoundException
import com.fasousa.vehicleplatform.infrastructure.client.exception.VehicleUnavailableException
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class ClientDtoAndExceptionTest {

    @Test
    fun `GIVEN purchase request WHEN created THEN should keep cpf`() {
        val request = PurchaseVehicleRequest(
            cpf = "12345678900"
        )

        assertEquals(
            "12345678900",
            request.cpf
        )
    }

    @Test
    fun `GIVEN sale response WHEN created THEN should keep all values`() {
        val saleDate = LocalDateTime.of(
            2026,
            7,
            12,
            18,
            30
        )

        val response = SaleResponse(
            id = 1L,
            vehicleId = 10L,
            cpf = "12345678900",
            paymentCode = "PAY-123",
            paymentStatus = "PENDING",
            saleDate = saleDate
        )

        assertEquals(1L, response.id)
        assertEquals(10L, response.vehicleId)
        assertEquals("12345678900", response.cpf)
        assertEquals("PAY-123", response.paymentCode)
        assertEquals("PENDING", response.paymentStatus)
        assertEquals(saleDate, response.saleDate)
    }

    @Test
    fun `GIVEN nullable sale response WHEN created THEN should accept null values`() {
        val response = SaleResponse(
            id = null,
            vehicleId = 10L,
            cpf = "12345678900",
            paymentCode = null,
            paymentStatus = null,
            saleDate = null
        )

        assertNull(response.id)
        assertNull(response.paymentCode)
        assertNull(response.paymentStatus)
        assertNull(response.saleDate)
    }

    @Test
    fun `GIVEN sale service exception WHEN created THEN should keep message and cause`() {
        val cause = IllegalStateException("Connection failed")

        val exception = SaleServiceException(
            message = "Sale service unavailable",
            cause = cause
        )

        assertEquals(
            "Sale service unavailable",
            exception.message
        )

        assertSame(
            cause,
            exception.cause
        )
    }

    @Test
    fun `GIVEN sale service exception without cause WHEN created THEN cause should be null`() {
        val exception = SaleServiceException(
            message = "Unexpected sale service error"
        )

        assertEquals(
            "Unexpected sale service error",
            exception.message
        )

        assertNull(exception.cause)
    }

    @Test
    fun `GIVEN vehicle not found exception WHEN created THEN should keep message`() {
        val exception = VehicleNotFoundException(
            "Vehicle not found"
        )

        assertEquals(
            "Vehicle not found",
            exception.message
        )
    }

    @Test
    fun `GIVEN vehicle unavailable exception WHEN created THEN should keep message`() {
        val exception = VehicleUnavailableException(
            "Vehicle unavailable"
        )

        assertEquals(
            "Vehicle unavailable",
            exception.message
        )
    }
}