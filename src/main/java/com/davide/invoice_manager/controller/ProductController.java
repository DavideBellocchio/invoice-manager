package com.davide.invoice_manager.controller;

import com.davide.invoice_manager.command.CreateProductCommand;
import com.davide.invoice_manager.command.UpdateProductCommand;
import com.davide.invoice_manager.domain.Product;
import com.davide.invoice_manager.dto.request.CreateProductRequest;
import com.davide.invoice_manager.dto.request.UpdateProductRequest;
import com.davide.invoice_manager.dto.response.ProductResponse;
import com.davide.invoice_manager.mapper.ProductMapper;
import com.davide.invoice_manager.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.findAll().stream().map(productMapper::toResponse).toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest createProductRequest) {
        CreateProductCommand command = productMapper.toCommand(createProductRequest);
        ProductResponse product = productMapper.toResponse(productService.addProduct(command));
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest updateProductRequest) {
        UpdateProductCommand command = productMapper.toCommand(updateProductRequest);
        ProductResponse product = productMapper.toResponse(productService.updateProduct(id, command));
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
