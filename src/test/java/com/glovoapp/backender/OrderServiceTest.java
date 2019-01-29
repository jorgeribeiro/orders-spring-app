package com.glovoapp.backender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class OrderServiceTest {
	@Autowired
	private OrderService orderService;

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
		assertEquals(2, orders.size());
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
	
	@Test
	void testOrderService() {
		assertNotNull(orderService);
	}
	
	@Test
	void testBoxOrders() {
		Courier courier = courierRepository.findById("courier-1");
		assertTrue(orderService.filterBoxOrders(courier.getBox(), orders.get(0).getDescription()));
	}
	
	@Test
	void testNotBoxOrders() {
		Courier courier = courierRepository.findById("courier-2");
		assertFalse(orderService.filterBoxOrders(courier.getBox(), orders.get(0).getDescription()));
	}
	
	@Test
	void testLongDistanceOrders() {
		Courier courier = courierRepository.findById("courier-1");
		assertTrue(orderService.filterLongDistanceOrders(courier.getVehicle(), courier.getLocation(), orders.get(0).getPickup()));
	}
	
	@Test
	void testSortOrders() {
		Courier courier = courierRepository.findById("courier-1");
		Order order1 = new Order()
				.withId("order-test-1")
				.withDescription("2x Kebab with Salad")
				.withFood(true)
				.withVip(true)
				.withPickup(new Location(41.40147420796175, 2.1814949595367445))
				.withDelivery(new Location(41.376240370620444, 2.1737334146979794));
		Order order2 = new Order()
				.withId("order-test-2")
				.withDescription("Keys")
				.withFood(false)
				.withVip(true)
				.withPickup(new Location(41.40481070052986, 2.174550073093567))
				.withDelivery(new Location(41.38487739165542, 2.181197030179494));
		Order order3 = new Order()
				.withId("order-test-3")
				.withDescription("1x Pizza with Fries")
				.withFood(true)
				.withVip(false)
				.withPickup(new Location(41.38333814358241, 2.1851196476884964))
				.withDelivery(new Location(41.38584206357778, 2.1667597138169112));
		Order order4 = new Order()
				.withId("order-test-4")
				.withDescription("2x Burger with Fries\\n1x Burger with Fries\\n2x Kebab with Salad")
				.withFood(true)
				.withVip(true)
				.withPickup(new Location(41.378414675479426, 2.180782725574193))
				.withDelivery(new Location(41.4043704453392, 2.1849609870501383));
		Order order5 = new Order()
				.withId("order-test-5")
				.withDescription("Envelope")
				.withFood(false)
				.withVip(false)
				.withPickup(new Location(41.378146797203584, 2.1668908527391872))
				.withDelivery(new Location(41.40079758788025, 2.1694627722267117));
		List<Order> orders = new ArrayList<Order>();
		// Correct sort order (distance, vip, food)
		orders.add(order1);
		orders.add(order4);
		orders.add(order2);
		orders.add(order3);				
		orders.add(order5);
		
		assertEquals(orders, orders.stream().sorted(orderService.sortOrders(courier.getLocation())).collect(Collectors.toList()));
	}
	
	@Test
	void testSortOrdersVipFirst() {
		Courier courier = courierRepository.findById("courier-1");
		Order order1 = new Order()
				.withId("order-test-1")
				.withDescription("2x Kebab with Salad")
				.withFood(true)
				.withVip(true)
				.withPickup(new Location(41.40147420796175, 2.1814949595367445))
				.withDelivery(new Location(41.376240370620444, 2.1737334146979794));
		Order order2 = new Order()
				.withId("order-test-2")
				.withDescription("Keys")
				.withFood(false)
				.withVip(true)
				.withPickup(new Location(41.40481070052986, 2.174550073093567))
				.withDelivery(new Location(41.38487739165542, 2.181197030179494));
		Order order3 = new Order()
				.withId("order-test-3")
				.withDescription("1x Pizza with Fries")
				.withFood(true)
				.withVip(false)
				.withPickup(new Location(41.38333814358241, 2.1851196476884964))
				.withDelivery(new Location(41.38584206357778, 2.1667597138169112));
		Order order4 = new Order()
				.withId("order-test-4")
				.withDescription("2x Burger with Fries\\n1x Burger with Fries\\n2x Kebab with Salad")
				.withFood(true)
				.withVip(true)
				.withPickup(new Location(41.378414675479426, 2.180782725574193))
				.withDelivery(new Location(41.4043704453392, 2.1849609870501383));
		Order order5 = new Order()
				.withId("order-test-5")
				.withDescription("Envelope")
				.withFood(false)
				.withVip(false)
				.withPickup(new Location(41.378146797203584, 2.1668908527391872))
				.withDelivery(new Location(41.40079758788025, 2.1694627722267117));
		List<Order> orders = new ArrayList<Order>();
		// Correct sort order (vip)
		orders.add(order1);
		orders.add(order4);
		orders.add(order2);
		orders.add(order3);				
		orders.add(order5);
		
		assertEquals(orders, orders.stream().sorted(orderService.prioritiseOrders("vip", courier.getLocation())).collect(Collectors.toList()));
	}
	
	@Test
	void testSortOrdersFoodFirst() {
		Courier courier = courierRepository.findById("courier-1");
		Order order1 = new Order()
				.withId("order-test-1")
				.withDescription("2x Kebab with Salad")
				.withFood(true)
				.withVip(true)
				.withPickup(new Location(41.40147420796175, 2.1814949595367445))
				.withDelivery(new Location(41.376240370620444, 2.1737334146979794));
		Order order2 = new Order()
				.withId("order-test-2")
				.withDescription("Keys")
				.withFood(false)
				.withVip(true)
				.withPickup(new Location(41.40481070052986, 2.174550073093567))
				.withDelivery(new Location(41.38487739165542, 2.181197030179494));
		Order order3 = new Order()
				.withId("order-test-3")
				.withDescription("1x Pizza with Fries")
				.withFood(true)
				.withVip(false)
				.withPickup(new Location(41.38333814358241, 2.1851196476884964))
				.withDelivery(new Location(41.38584206357778, 2.1667597138169112));
		Order order4 = new Order()
				.withId("order-test-4")
				.withDescription("2x Burger with Fries\\n1x Burger with Fries\\n2x Kebab with Salad")
				.withFood(true)
				.withVip(true)
				.withPickup(new Location(41.378414675479426, 2.180782725574193))
				.withDelivery(new Location(41.4043704453392, 2.1849609870501383));
		Order order5 = new Order()
				.withId("order-test-5")
				.withDescription("Envelope")
				.withFood(false)
				.withVip(false)
				.withPickup(new Location(41.378146797203584, 2.1668908527391872))
				.withDelivery(new Location(41.40079758788025, 2.1694627722267117));
		List<Order> orders = new ArrayList<Order>();
		// Correct sort order (food)
		orders.add(order1);
		orders.add(order4);
		orders.add(order3);
		orders.add(order2);					
		orders.add(order5);
		
		assertEquals(orders, orders.stream().sorted(orderService.prioritiseOrders("food", courier.getLocation())).collect(Collectors.toList()));
	}

}
