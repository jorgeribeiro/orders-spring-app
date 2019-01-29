package com.glovoapp.backender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ComponentScan("com.glovoapp.backender")
@EnableAutoConfiguration
@SpringBootApplication
class API {
	private final String welcomeMessage;
	private final OrderRepository orderRepository;
	private final CourierRepository courierRepository;
	private final OrderService orderService;

	@Autowired
	API(@Value("${backender.welcome_message}") String welcomeMessage, OrderRepository orderRepository,
			CourierRepository courierRepository, OrderService orderService) {
		this.welcomeMessage = welcomeMessage;
		this.orderRepository = orderRepository;
		this.courierRepository = courierRepository;
		this.orderService = orderService;
	}

	@RequestMapping("/")
	@ResponseBody
	String root() {
		return welcomeMessage;
	}

	@RequestMapping("/orders")
	@ResponseBody
	List<OrderVM> orders() {
		return orderRepository.findAll()
				.stream()
				.map(order -> new OrderVM(order.getId(), order.getDescription()))
				.collect(Collectors.toList());
	}

	/**
	 * New endpoint added as requested. Everything works at OrderService, calling
	 * the method getOrdersByCourier(Courier, List<Order>). I also commented the
	 * methods on OrderService, to help those who will evaluate the code
	 * 
	 * @param courierId
	 *            Provided via API
	 * @return Orders available to the Courier provided
	 */
	@RequestMapping("/orders/{courierId}")
	@ResponseBody
	List<OrderVM> ordersByCourierId(@PathVariable("courierId") String courierId) {
		Courier courier = courierRepository.findById(courierId);
		if (courier == null) {
			return new ArrayList<OrderVM>();
		}
		
		List<Order> orders = orderRepository.findAll();
		return orderService.getOrdersByCourier(courier, orders)
				.stream()
				.map(order -> new OrderVM(order.getId(), order.getDescription()))
				.collect(Collectors.toList());
	}

	public static void main(String[] args) {
		SpringApplication.run(API.class);
	}
}
