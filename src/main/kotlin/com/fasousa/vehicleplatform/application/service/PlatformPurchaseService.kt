package com.fasousa.vehicleplatform.application.service

import com.fasousa.vehicleplatform.domain.model.PaymentEvent
import com.fasousa.vehicleplatform.domain.model.PaymentEventStatus
import com.fasousa.vehicleplatform.infrastructure.client.VehicleSaleServiceClient
import com.fasousa.vehicleplatform.infrastructure.persistence.mapper.PaymentEventMapper
import com.fasousa.vehicleplatform.infrastructure.persistence.repository.PaymentEventMongoRepository
import com.fasousa.vehicleplatform.presentation.dto.PlatformPurchaseResponse
import org.springframework.stereotype.Service

@Service
class PlatformPurchaseService(
    private val vehicleSaleServiceClient: VehicleSaleServiceClient,
    private val paymentEventMongoRepository: PaymentEventMongoRepository
) {

    fun purchase(vehicleId: Long, cpf: String): PlatformPurchaseResponse {
        val saleResponse = vehicleSaleServiceClient.purchaseVehicle(vehicleId, cpf)

        val event = PaymentEvent(
            vehicleId = vehicleId,
            cpf = cpf,
            paymentCode = saleResponse.paymentCode,
            status = PaymentEventStatus.CREATED
        )

        val savedEvent = paymentEventMongoRepository.save(
            PaymentEventMapper.toDocument(event)
        )

        return PlatformPurchaseResponse(
            saleId = saleResponse.id,
            vehicleId = saleResponse.vehicleId,
            cpf = saleResponse.cpf,
            paymentCode = saleResponse.paymentCode,
            paymentStatus = saleResponse.paymentStatus,
            platformEventId = savedEvent.id
        )
    }
}
