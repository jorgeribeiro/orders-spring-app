package com.glovoapp.backender;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AppConfiguration {
	@Bean OrderService orderService (@Value("#{'${backender.order.box}'.split(',')}") List<String> boxOrders, 
			@Value("${backender.order.long_distance_limit}") Double longDistanceLimit,
			@Value("#{'${backender.order.sort}'.split(',')}") List<String> ordersSort,
			@Value("${backender.order.distance_slot}") Double distanceSlot) {
		return new OrderService(boxOrders, longDistanceLimit, ordersSort, distanceSlot);
	}
}
