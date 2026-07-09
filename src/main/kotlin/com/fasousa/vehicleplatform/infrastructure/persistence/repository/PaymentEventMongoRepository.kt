package com.fasousa.vehicleplatform.infrastructure.persistence.repository

import com.fasousa.vehicleplatform.infrastructure.persistence.entity.PaymentEventDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface PaymentEventMongoRepository : MongoRepository<PaymentEventDocument, String> {
    fun findByVehicleId(vehicleId: Long): List<PaymentEventDocument>
}
