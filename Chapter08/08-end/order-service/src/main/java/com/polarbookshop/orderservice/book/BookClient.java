package com.polarbookshop.orderservice.book;

import java.time.Duration;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class BookClient {

	private static final String BOOKS_ROOT_API = "/books/";
	/*
	WebClient is the modern alternative to RestTemplate. It provides
	blocking and non-blocking I/O
	 */
	private final WebClient webClient; // baseUrl(clientProperties.catalogServiceUri().toString())

	// Bean injected from ClientConfig class
	public BookClient(WebClient webClient) {
		this.webClient = webClient;
	}

	// Call Catalog-service
	public Mono<Book> getBookByIsbn(String isbn) {
		return webClient
				.get()
				.uri(BOOKS_ROOT_API + isbn)
				.retrieve()
				.bodyToMono(Book.class)
				// Set a timeout of 3 seconds for the GET Request
				.timeout(Duration.ofSeconds(3), Mono.empty())
				// Fallback after timeout, returns Empty Mono
				.onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
				// retry mechanism, 3 attempts
				.retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
				// catch the exception and return an empty object
				.onErrorResume(Exception.class, exception -> Mono.empty());
	}

}
