package com.glovoapp.backender;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OrderServiceTest {
	private static OrderRepository orderRepository;
	private static CourierRepository courierRepository;
	private static OrderService orderService;
	private static List<Order> orders;
	
	@BeforeAll
	static void setup() {
		orderRepository = new OrderRepository();
		courierRepository = new CourierRepository();
		orderService = new OrderService();
		orders = orderRepository.findAll();
	}
	
	@Test
	void testFilterBoxOrders() {
		Courier courier = new Courier().withId("courier-2")
				.withBox(false)
				.withName("Jorge Ribeiro")
				.withVehicle(Vehicle.BICYCLE)
				.withLocation(new Location(41.3965463, 2.1963997));
		assertFalse(orderService.filterBoxOrders(courier.getBox(), orders.get(0).getDescription()));
	}
	
	@Test
	void testFilterLongDistanceOrders() {
		Courier courier = courierRepository.findById("courier-1");
		assertTrue(orderService.filterLongDistanceOrders(courier.getVehicle(), courier.getLocation(), orders.get(0).getPickup()));
	}
}
