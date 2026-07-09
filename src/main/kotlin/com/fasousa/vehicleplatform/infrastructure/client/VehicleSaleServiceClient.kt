package com.fasousa.vehicleplatform.infrastructure.client

import com.fasousa.vehicleplatform.infrastructure.client.dto.PurchaseVehicleRequest
import com.fasousa.vehicleplatform.infrastructure.client.dto.SaleResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class VehicleSaleServiceClient(
    @param:Value("\${vehicle-sale-service.base-url}")
    private val baseUrl: String
) {

    private val webClient: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build()

    fun purchaseVehicle(vehicleId: Long, cpf: String): SaleResponse {
        return webClient.post()
            .uri("/api/vehicles/$vehicleId/purchase")
            .bodyValue(PurchaseVehicleRequest(cpf = cpf))
            .retrieve()
            .bodyToMono(SaleResponse::class.java)
            .block()
            ?: throw IllegalStateException("Empty response from vehicle-sale-service")
    }
}
