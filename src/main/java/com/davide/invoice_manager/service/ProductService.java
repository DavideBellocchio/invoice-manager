package com.davide.invoice_manager.service;

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

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        Product p = productRepository.findById(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + product.getId()));
        if (product.getName() != null) {
            p.setName(product.getName());
        }
        if (product.getDescription() != null) {
            p.setDescription(product.getDescription());
        }
        if (product.getPrice() != null) {
            p.setPrice(product.getPrice());
        }
        return productRepository.save(p);
    }

    public void deleteProduct(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(p);
    }

}
