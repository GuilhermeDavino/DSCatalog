package com.devsuperior.dscatalog.tests;

import java.time.Instant;

import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

public class ProductFactory {
	
	
	public static Product createProduct() {
		Product product = new Product(1L, "Iphone", 800.0, "Good Phone", "https://img.com/img", Instant.parse("2020-03-15T14:30:30Z"));
		product.getCategories().add(createCategory());
		return product;
		
	}
	
	public static ProductDTO createDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}
	
	public static Category createCategory() {
		return new Category(1L, "Electronics");
	}
}
