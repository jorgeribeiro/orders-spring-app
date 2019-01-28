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
		List<Order> sOrders = orders
				.stream()
				.filter(order -> filterBoxOrders(courier.getBox(), order.getDescription()))
				.filter(order -> filterLongDistanceOrders(courier.getVehicle(), courier.getLocation(), order.getPickup()))
				.sorted(sortOrders(courier.getLocation()))
				.collect(Collectors.toList());
		
		sOrders.forEach(o -> System.out.println(compareSlots(DistanceCalculator.calculateDistance(courier.getLocation(), o.getPickup())) + " " + 
				DistanceCalculator.calculateDistance(courier.getLocation(), o.getPickup()) + " " +
				o.getVip() + " " + o.getFood()));
		
		return sOrders;
	}
	
	private Boolean filterBoxOrders(Boolean hasBox, String orderDescription) {
		return hasBox || !boxOrders.stream().anyMatch(boxWord -> StringUtils.containsIgnoreCase(orderDescription, boxWord));
	}
	
	private Boolean filterLongDistanceOrders(Vehicle vehicleType, Location courierLocation, Location orderPickup) {
		return vehicleType == Vehicle.ELECTRIC_SCOOTER || 
				vehicleType == Vehicle.MOTORCYCLE ||
				DistanceCalculator.calculateDistance(courierLocation, orderPickup) <= longDistanceLimit;
	}	
	
	private Comparator<Order> sortOrders(Location courierLocation) {
		return prioritiseOrders(ordersSort.get(0), courierLocation)
				.thenComparing(prioritiseOrders(ordersSort.get(1), courierLocation))
				.thenComparing(prioritiseOrders(ordersSort.get(2), courierLocation))
				.thenComparingDouble((Order o) -> DistanceCalculator.calculateDistance(courierLocation, o.getPickup()));
	}

	private Comparator<Order> prioritiseOrders(String sortType, Location courierLocation) {
		if (sortType.equals("closest")) {
			return Comparator.comparingInt((Order o) -> compareSlots(DistanceCalculator.calculateDistance(courierLocation, o.getPickup())));
		} else if (sortType.equals("vip")) {
			return Comparator.comparing(Order::getVip).reversed();									
		} else if (sortType.equals("food")) {
			return Comparator.comparing(Order::getFood).reversed();
		}
		return null;
	}
	
	private Integer compareSlots(Double distance) {
		if (distance < distanceSlot) {
			return 0;
		} else if (distance < distanceSlot * 2) {
			return 1;
		} else {
			return 2;
		}
	}
	
}
