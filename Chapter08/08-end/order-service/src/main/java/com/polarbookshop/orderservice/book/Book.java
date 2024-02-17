package com.polarbookshop.orderservice.book;

// DTO for storing Book information
public record Book(
	String isbn,
	String title,
	String author,
	Double price
){}
