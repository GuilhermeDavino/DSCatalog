package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
public class ProductServiceIT {
	
	@Autowired
	private ProductService service;
	
	@Autowired
	private ProductRepository repository;
	
	private Long existingId;
	private Long nonExistingId;
	private Long totalCountsProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = -1L;
		totalCountsProducts = 25L;
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		
		service.delete(existingId);
		Assertions.assertEquals(totalCountsProducts - 1, repository.count());
	}
	
	@Test
	public void deleteShoudThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
}
