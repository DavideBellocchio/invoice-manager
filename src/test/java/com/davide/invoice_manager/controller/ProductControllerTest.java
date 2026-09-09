package com.davide.invoice_manager.controller;

import com.davide.invoice_manager.domain.Product;
import com.davide.invoice_manager.dto.response.ProductResponse;
import com.davide.invoice_manager.mapper.ProductMapper;
import com.davide.invoice_manager.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
