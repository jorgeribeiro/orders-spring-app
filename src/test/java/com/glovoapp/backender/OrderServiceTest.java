package com.glovoapp.backender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OrderServiceTest {
	private static OrderRepository orderRepository;
	private static CourierRepository courierRepository;
	private static List<Order> orders;
	private static List<Courier> couriers;
	
	@BeforeAll
	static void setup() {
		orderRepository = new OrderRepository();
		courierRepository = new CourierRepository();
		orders = orderRepository.findAll();
		couriers = courierRepository.findAll();
	}
	
	@Test
    void testGetAllOrders() {
        assertEquals(1, orders.size());
	}
	
	@Test
    void testGetAllCouriers() {
        assertEquals(2, couriers.size());
	}
	
	@Test
	void testFindOrdersWithInvalidCourier() {
		Courier courier = courierRepository.findById("courier-3");
		assertNull(courier);
	}
	
	
}
