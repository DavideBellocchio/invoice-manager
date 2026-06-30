package com.davide.invoice_manager.service;

import com.davide.invoice_manager.command.CreateProductCommand;
import com.davide.invoice_manager.command.UpdateProductCommand;
import com.davide.invoice_manager.domain.Product;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product addProduct(CreateProductCommand command) {
        Product product = new Product();
        product.setName(command.name());
        product.setDescription(command.description());
        product.setPrice(command.price());
        return productRepository.save(product);
    }

    public Product updateProduct(Long productId, UpdateProductCommand command) {
        Product p = findById(productId);
        if (command.price() != null) {
            p.setPrice(command.price() );
        }
        return productRepository.save(p);
    }

    public void deleteProduct(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(p);
    }

}
