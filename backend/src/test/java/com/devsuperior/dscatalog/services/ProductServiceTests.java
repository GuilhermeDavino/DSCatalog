package com.devsuperior.dscatalog.services;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.ProductFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;

	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private Category category;
	private ProductDTO dto;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		product = ProductFactory.createProduct();
		category = ProductFactory.createCategory();
		page = new PageImpl<>(List.of(product));
		dto = ProductFactory.createDTO();
		
		Mockito.when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category));
		Mockito.doThrow(EntityNotFoundException.class).when(categoryRepository).findById(nonExistingId);
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getReferenceById(nonExistingId);
		
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
		Mockito.when(repository.save(product)).thenReturn(product);
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistingId);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		
		Mockito.when(repository.existsById(existingId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
		
		Mockito.when(repository.existsById(dependentId)).thenReturn(true);
		Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
		
		
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.update(existingId, dto);
		Assertions.assertNotNull(result);
		Mockito.verify(repository).getReferenceById(existingId);
		Mockito.verify(categoryRepository, atLeast(1)).findById(existingId);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, dto);
			Mockito.verify(repository).getReferenceById(1000L);
		});
	}
	
	@Test
	public void findByIdShouldThrowNotFoundExceptionWhenIdDoNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.findById(existingId);
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 5);
		Page<ProductDTO> result = service.findAllPaged(pageable);
		Assertions.assertNotNull(result);
		Mockito.verify(repository, times(1)).findAll(pageable);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
			Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
		});
	}



	@Test
	public void deleteShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
}
