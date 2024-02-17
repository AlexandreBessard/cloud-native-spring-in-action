package com.polarbookshop.orderservice.order.domain;

import com.polarbookshop.orderservice.book.Book;
import com.polarbookshop.orderservice.book.BookClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

	private final BookClient bookClient;
	private final OrderRepository orderRepository;

	public OrderService(BookClient bookClient, OrderRepository orderRepository) {
		this.bookClient = bookClient;
		this.orderRepository = orderRepository;
	}

	// A Flux is used to publish multiple orders (0..N)
	public Flux<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	// Mono<T>—Represents a single asynchronous value or empty result (0..1)
	public Mono<Order> submitOrder(String isbn, int quantity) {
		// Calls the Catalog Service to check the book’s availability
		return bookClient.getBookByIsbn(isbn)
				// If the book is available, it accepts the order.
				.map(book -> buildAcceptedOrder(book, quantity))
				// When an order is rejected, we only specify ISBN, quantity, and status. Spring Data takes care of
				// adding identifier, version, and audit metadata.
				.defaultIfEmpty(buildRejectedOrder(isbn, quantity))
				// Saves the Order object produced asynchronously by the previous step of the reactive stream into the
				// database
				// flatMap() maps from a Java type to another reactive stream.
				.flatMap(orderRepository::save);
	}

	public static Order buildAcceptedOrder(Book book, int quantity) {
		return Order.of(book.isbn(), book.title() + " - " + book.author(),
				book.price(), quantity, OrderStatus.ACCEPTED);
	}

	public static Order buildRejectedOrder(String bookIsbn, int quantity) {
		return Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
	}

}
