package com.davide.invoice_manager.controller;

import com.davide.invoice_manager.command.CreateProductCommand;
import com.davide.invoice_manager.command.UpdateProductCommand;
import com.davide.invoice_manager.domain.Product;
import com.davide.invoice_manager.dto.request.CreateProductRequest;
import com.davide.invoice_manager.dto.request.UpdateProductRequest;
import com.davide.invoice_manager.dto.response.ProductResponse;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.mapper.ProductMapper;
import com.davide.invoice_manager.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductMapper productMapper;

    private Product testProduct;

    @BeforeEach
    public void init(){
        testProduct = new Product();

    }

    @Test
    public void shouldReturnAllProducts() throws Exception {
        List<Product> products = List.of(testProduct);
        ProductResponse response = new ProductResponse(1L,"licenza","annuale", new BigDecimal("10"));

        when(productService.findAll()).thenReturn(products);
        when(productMapper.toResponse(testProduct)).thenReturn(response);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("licenza"))
                .andExpect(jsonPath("$[0].price").value(10))
                .andExpect(jsonPath("$.length()").value(1));

    }

    @Test
    public void shouldReturnProduct_whenIdExists() throws Exception {
        ProductResponse response = new ProductResponse(1L,"licenza","annuale", new BigDecimal("10"));
        when(productService.findById(1L)).thenReturn(testProduct);
        when(productMapper.toResponse(testProduct)).thenReturn(response);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("licenza"))
                .andExpect(jsonPath("$.price").value(10));

    }

    @Test
    public void shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        when(productService.findById(1L)).thenThrow(new ResourceNotFoundException("Product not found with id: " + 1L));
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateProduct_whenRequestIsValid() throws Exception {
        CreateProductCommand command = new CreateProductCommand("licenza", "annuale", new BigDecimal("10"));
        ProductResponse response = new ProductResponse(1L,"licenza","annuale", new BigDecimal("10"));
        when(productMapper.toCommand(any(CreateProductRequest.class))).thenReturn(command);
        when(productService.addProduct(any(CreateProductCommand.class))).thenReturn(testProduct);
        when(productMapper.toResponse(any(Product.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"licenza\", \"description\": \"annuale\", \"price\": 10 }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("licenza"))
                .andExpect(jsonPath("$.price").value(10));
    }

    @Test
    public void shouldReturnBadRequest_whenNameIsBlank() throws Exception {

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"\", \"description\": \"annuale\", \"price\": 10 }"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).addProduct(any());
    }

    @Test
    public void shouldUpdateProduct_whenRequestIsValid() throws Exception {
        UpdateProductCommand command = new UpdateProductCommand(new BigDecimal(30));
        ProductResponse response = new ProductResponse(1L,"licenza","annuale", new BigDecimal(30));
        when(productMapper.toCommand(any(UpdateProductRequest.class))).thenReturn(command);
        when(productService.updateProduct(1L,command)).thenReturn(testProduct);
        when(productMapper.toResponse(any(Product.class))).thenReturn(response);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"price\": 30 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("licenza"))
                .andExpect(jsonPath("$.price").value(30));
    }

    @Test
    public void shouldReturnNotFound_whenUpdatingMissingId() throws Exception {
        UpdateProductCommand command = new UpdateProductCommand(new BigDecimal(30));
        when(productMapper.toCommand(any(UpdateProductRequest.class))).thenReturn(command);
        when(productService.updateProduct(eq(1L), any(UpdateProductCommand.class))).thenThrow(new ResourceNotFoundException("Product not found with id: " + 1L));
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"price\": 30 }"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteProduct_whenIdExists() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(productService, Mockito.times(1)).deleteProduct(1L);
    }

    @Test
    public void shouldReturnNotFound_whenDeletingMissingId() throws Exception {
        doThrow(new ResourceNotFoundException("Product not found with id: 1"))
                .when(productService).deleteProduct(1L);
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNotFound());
    }
}
