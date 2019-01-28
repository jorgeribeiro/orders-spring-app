package com.glovoapp.backender;

import java.util.Comparator;
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
	
	@Value("${backender.order.long_distance_limit}")
	Double longDistanceLimit;
	
	@Value("#{'${backender.order.sort}'.split(',')}")
	List<String> ordersSort;
	
	@Value("${backender.order.distance_slot}")
	Double distanceSlot;
	
	List<Order> getOrdersByCourier(Courier courier, List<Order> orders) {
		return orders
				.stream()
				.filter(order -> filterBoxOrders(courier.getBox(), order.getDescription()))
				.filter(order -> filterLongDistanceOrders(courier.getVehicle(), courier.getLocation(), order.getPickup()))
				.sorted(sortOrdersByDistance(courier.getLocation()))
				.collect(Collectors.toList());
	}
	
	private Boolean filterBoxOrders(Boolean hasBox, String orderDescription) {
		return hasBox || !boxOrders.stream().anyMatch(boxWord -> StringUtils.containsIgnoreCase(orderDescription, boxWord));
	}
	
	private Boolean filterLongDistanceOrders(Vehicle vehicleType, Location courierLocation, Location orderPickup) {
		return vehicleType == Vehicle.ELECTRIC_SCOOTER || 
				vehicleType == Vehicle.MOTORCYCLE ||
				DistanceCalculator.calculateDistance(courierLocation, orderPickup) <= longDistanceLimit;
	}
	
	private Comparator<Order> sortOrdersByDistance(Location courierLocation) {
		return Comparator.comparingDouble((Order o) -> (DistanceCalculator.calculateDistance(courierLocation, o.getPickup())));
	}
	
}
