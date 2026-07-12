package com.fasousa.vehicleplatform.infrastructure.client

import com.fasousa.vehicleplatform.infrastructure.client.dto.PurchaseVehicleRequest
import com.fasousa.vehicleplatform.infrastructure.client.dto.SaleResponse
import com.fasousa.vehicleplatform.infrastructure.client.exception.SaleServiceException
import com.fasousa.vehicleplatform.infrastructure.client.exception.VehicleNotFoundException
import com.fasousa.vehicleplatform.infrastructure.client.exception.VehicleUnavailableException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class VehicleSaleServiceClient(
    @param:Value("\${vehicle-sale-service.base-url}")
    private val baseUrl: String,
    @param:Value("\${vehicle-sale-service.timeout:5000}")
    private val timeoutMs: Long = 5000
) {

    private val webClient: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build()

    fun purchaseVehicle(vehicleId: Long, cpf: String): SaleResponse {
        return webClient.post()
            .uri("/api/vehicles/$vehicleId/purchase")
            .bodyValue(PurchaseVehicleRequest(cpf = cpf))
            .retrieve()
            .onStatus(
                { it == HttpStatus.NOT_FOUND },
                { Mono.error(VehicleNotFoundException("Vehicle with ID $vehicleId not found")) }
            )
            .onStatus(
                { it == HttpStatus.CONFLICT },
                { Mono.error(VehicleUnavailableException("Vehicle with ID $vehicleId is not available for purchase")) }
            )
            .onStatus(
                { it.is5xxServerError },
                { Mono.error(SaleServiceException("Sale service temporarily unavailable")) }
            )
            .bodyToMono(SaleResponse::class.java)
            .block(Duration.ofMillis(timeoutMs))
            ?: throw SaleServiceException("Empty response from vehicle-sale-service")
    }
}
