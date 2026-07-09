package com.fasousa.vehicleplatform.presentation.controller

import com.fasousa.vehicleplatform.application.service.PlatformPurchaseService
import com.fasousa.vehicleplatform.presentation.dto.PlatformPurchaseRequest
import com.fasousa.vehicleplatform.presentation.dto.PlatformPurchaseResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/platform")
class PlatformPurchaseController(
    private val platformPurchaseService: PlatformPurchaseService
) {

    @PostMapping("/vehicles/{vehicleId}/purchase")
    @ResponseStatus(HttpStatus.CREATED)
    fun purchaseVehicle(
        @PathVariable vehicleId: Long,
        @RequestBody request: PlatformPurchaseRequest
    ): PlatformPurchaseResponse {
        return platformPurchaseService.purchase(
            vehicleId = vehicleId,
            cpf = request.cpf
        )
    }
}
