package com.davide.invoice_manager.mapper;

import com.davide.invoice_manager.command.CreateProductCommand;
import com.davide.invoice_manager.command.UpdateProductCommand;
import com.davide.invoice_manager.domain.Product;
import com.davide.invoice_manager.dto.request.CreateProductRequest;
import com.davide.invoice_manager.dto.request.UpdateProductRequest;
import com.davide.invoice_manager.dto.response.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public CreateProductCommand toCommand(CreateProductRequest product) {
        return new CreateProductCommand(
                product.name(),
                product.description(),
                product.price()
        );
    }

    public UpdateProductCommand toCommand(UpdateProductRequest product) {
        return new UpdateProductCommand(product.price());
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
