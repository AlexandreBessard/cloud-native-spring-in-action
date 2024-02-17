package com.polarbookshop.orderservice.order.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("orders") // Mapping between java object and the table
public record Order (

	@Id
	Long id,

	String bookIsbn, // book_isbn
	String bookName,
	Double bookPrice,
	Integer quantity,
	OrderStatus status,

	// Metadata
	@CreatedDate
	Instant createdDate,

	@LastModifiedDate
	Instant lastModifiedDate,

	@Version
	int version // Handling concurrent updates and using optimistic locking
){

	public static Order of(String bookIsbn, String bookName, Double bookPrice, Integer quantity, OrderStatus status) {
		return new Order(null, bookIsbn, bookName, bookPrice, quantity, status, null, null, 0);
	}

}
