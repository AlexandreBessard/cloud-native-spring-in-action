package com.polarbookshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

	/*
	An object auto-configured by Spring Boot to build WebClient beans
	 */
	@Bean
	WebClient webClient(ClientProperties clientProperties, WebClient.Builder webClientBuilder) {
		return webClientBuilder
				// Configures the WebClient base URL to the Catalog Service URL defined as a custom property
				.baseUrl(clientProperties.catalogServiceUri().toString())
				.build();
	}

}
