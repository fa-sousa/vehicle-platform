package com.fasousa.vehicleplatform

import com.fasousa.vehicleplatform.infrastructure.persistence.repository.PaymentEventMongoRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test

@SpringBootTest(
	properties = [
		"server.port=0",
		"vehicle-sale-service.base-url=http://localhost:8080"
	]
)
@ActiveProfiles("test")
class VehiclePlatformApplicationTests {

	@MockitoBean
	lateinit var paymentEventMongoRepository: PaymentEventMongoRepository

	@Test
	fun contextLoads() {
	}
}
