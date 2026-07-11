package com.fasousa.vehicleplatform

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
	properties = [
		"spring.data.mongodb.uri=mongodb://localhost:27017/vehicle_platform_test",
		"vehicle-sale-service.base-url=http://localhost:8080"
	]
)
class VehiclePlatformApplicationTests {

	@Test
	fun contextLoads() {}
}
