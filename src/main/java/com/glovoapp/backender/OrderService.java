package com.glovoapp.backender;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

@Service
@EnableAutoConfiguration
class OrderService {
	@Value("#{'${backender.order.box}'.split(',')}")
	List<String> boxOrders;
	
	List<Order> getOrdersByCourier(Courier courier, List<Order> orders) {
		return orders
				.stream()
				.filter(order -> filterBoxOrders(courier.getBox(), order.getDescription()))
				.collect(Collectors.toList());
	}
	
	private Boolean filterBoxOrders(Boolean hasBox, String description) {
		return hasBox || !boxOrders.stream().anyMatch(boxWord -> StringUtils.containsIgnoreCase(description, boxWord));
	}
	
}
