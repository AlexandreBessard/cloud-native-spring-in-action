package com.polarbookshop.orderservice.config;

import java.net.URI;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "polar")
public record ClientProperties (

	@NotNull
	URI catalogServiceUri // mapped from catalog-service-uri key property

){}
