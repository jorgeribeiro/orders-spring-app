package com.glovoapp.backender;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class OrderService {
	List<String> boxOrders;	
	Double longDistanceLimit;
	List<String> ordersSort;
	Double distanceSlot;
	
	@Autowired
	public OrderService(@Value("#{'${backender.order.box}'.split(',')}") List<String> boxOrders, 
			@Value("${backender.order.long_distance_limit}") Double longDistanceLimit, 
			@Value("#{'${backender.order.sort}'.split(',')}") List<String> ordersSort,
			@Value("${backender.order.distance_slot}") Double distanceSlot) {
		this.boxOrders = boxOrders;
		this.longDistanceLimit = longDistanceLimit;
		this.ordersSort = ordersSort;
		this.distanceSlot = distanceSlot;		
	}

	/**
	 * Main method of this Service class. It returns the orders given the courierId,
	 * filtering box orders and long distance orders, as well as sorting following
	 * the order sort provided via application.properties
	 * 
	 * @param courier
	 *            Courier fetched via API
	 * @param orders
	 *            A full list of all orders
	 * @return A list of Orders available to this courier
	 */
	List<Order> getOrdersByCourier(Courier courier, List<Order> orders) {
		return orders
				.stream()
				.filter(order -> filterBoxOrders(courier.getBox(), order.getDescription()))
				.filter(order -> filterLongDistanceOrders(courier.getVehicle(), courier.getLocation(), order.getPickup()))
				.sorted(sortOrders(courier.getLocation()))
				.collect(Collectors.toList());
	}
	
	/**
	 * This method checks if the Courier has a box, if it hasn't, test if the order
	 * description contains the words provided via application.properties
	 * 
	 * @param hasBox
	 *            Attribute from Courier
	 * @param orderDescription
	 *            Attribute from Order
	 * @return True if the courier can see the order
	 */
	Boolean filterBoxOrders(Boolean hasBox, String orderDescription) {
		return hasBox || !boxOrders.stream().anyMatch(boxWord -> StringUtils.containsIgnoreCase(orderDescription, boxWord));
	}
	
	/**
	 * This method checks long distances trips, and returns whether a courier is
	 * able to pickup it or not
	 * 
	 * @param vehicleType
	 *            Attribute from Courier
	 * @param courierLocation
	 *            Attribute from Courier
	 * @param orderPickup
	 *            Attribute from Order
	 * @return True if the courier can see the order
	 */
	Boolean filterLongDistanceOrders(Vehicle vehicleType, Location courierLocation, Location orderPickup) {
		return vehicleType == Vehicle.ELECTRIC_SCOOTER || 
				vehicleType == Vehicle.MOTORCYCLE ||
				DistanceCalculator.calculateDistance(courierLocation, orderPickup) <= longDistanceLimit;
	}	
	
	/**
	 * Sort the orders, based on the property provided via application.properties.
	 * After the three types of sorts, sort by distance
	 * 
	 * @param courierLocation
	 *            Attribute from Courier
	 * @return A comparator capable of sorting orders with 3 different properties
	 */
	Comparator<Order> sortOrders(Location courierLocation) {
		return prioritiseOrders(ordersSort.get(0), courierLocation)
				.thenComparing(prioritiseOrders(ordersSort.get(1), courierLocation))
				.thenComparing(prioritiseOrders(ordersSort.get(2), courierLocation))
				.thenComparingDouble((Order o) -> DistanceCalculator.calculateDistance(courierLocation, o.getPickup()));
	}

	/**
	 * Method used by sortOrders(Location). It prioritise the orders correctly,
	 * following the rule from application.properties
	 * 
	 * @param sortType
	 *            The type of sort used when called
	 * @param courierLocation
	 *            Attribute from Courier. Used only when handling distances
	 * @return A comparator to each type of sort
	 */
	Comparator<Order> prioritiseOrders(String sortType, Location courierLocation) {
		if (sortType.equals("closest")) {
			return Comparator.comparingInt(
					(Order o) -> compareSlots(DistanceCalculator.calculateDistance(courierLocation, o.getPickup())));
		} else if (sortType.equals("vip")) {
			return Comparator.comparing(Order::getVip).reversed();
		} else if (sortType.equals("food")) {
			return Comparator.comparing(Order::getFood).reversed();
		}
		return null;
	}

	/**
	 * Method used by prioritiseOrders(String, Location). It creates slots based on
	 * the distance property from application.properties
	 * 
	 * @param distance
	 *            The distance between the Courier and Order Pickup
	 * @return A specific slot, based on the distance calculated
	 */
	Integer compareSlots(Double distance) {
		if (distance < distanceSlot) {
			return 0;
		} else if (distance < distanceSlot * 2) {
			return 1;
		} else {
			return 2;
		}
	}
	
}
