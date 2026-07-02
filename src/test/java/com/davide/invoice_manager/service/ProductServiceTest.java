package com.davide.invoice_manager.service;

import com.davide.invoice_manager.command.CreateProductCommand;
import com.davide.invoice_manager.command.UpdateProductCommand;
import com.davide.invoice_manager.domain.Product;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private List<Product> testProducts;
    private CreateProductCommand createCommand;
    private UpdateProductCommand updateCommand;

    @BeforeEach
    public void init(){
        testProduct = new Product(
                1L,
                "prodottoProva",
                "descrizioneProva",
                new BigDecimal("10"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        testProducts = List.of(
                new Product(
                        1L,
                        "prodottoProva1",
                        "descrizioneProva1",
                        new BigDecimal("10"),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ),
                new Product(
                        2L,
                        "prodottoProva2",
                        "descrizioneProva2",
                        new BigDecimal("20"),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
        createCommand = new CreateProductCommand(
                testProduct.getName(),
                testProduct.getDescription(),
                testProduct.getPrice()
        );

        updateCommand = new UpdateProductCommand(
                new BigDecimal("20")
        );

    }

    @Test
    public void findById_shouldReturnProduct_whenProductExists(){

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        Product product = productService.findById(1L);
        Assertions.assertEquals(product.getId(), testProduct.getId());
        Assertions.assertEquals(product.getName(), testProduct.getName());
        Assertions.assertEquals(product.getDescription(), testProduct.getDescription());
        Assertions.assertEquals(0,product.getPrice().compareTo(testProduct.getPrice()));
    }
    @Test
    public void findById_shouldReturnException_whenProductDoesNotExist(){
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.findById(1L));

    }
    @Test
    public void findAll_shouldReturnAllProducts_whenProductsExist(){
        Mockito.when(productRepository.findAll()).thenReturn(testProducts);
        List<Product> products = productService.findAll();
        Assertions.assertEquals(testProducts, products);
    }
    @Test
    public void findByName_shouldReturnProduct_whenProductExists() {
        List<Product> matchProd = List.of(testProducts.getFirst());
        Mockito.when(productRepository.findByName("prodottoProva1"))
                .thenReturn(matchProd);
        List<Product> products = productService.findByName("prodottoProva1");
        Assertions.assertEquals(matchProd, products);
    }
    @Test
    public void addProduct_shouldSaveAndReturnProduct() {
        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(testProduct);
        Product product = productService.addProduct(createCommand);
        Assertions.assertEquals(product.getId(), testProduct.getId());
        Assertions.assertEquals(product.getName(), testProduct.getName());
        Assertions.assertEquals(product.getDescription(), testProduct.getDescription());
        Mockito.verify(productRepository, Mockito.times(1)).save(Mockito.any(Product.class));
    }
    @Test
    public void updateProduct_shouldReturnException_whenProductDoesNotExist() {
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, updateCommand));
    }
    @Test
    public void updateProduct_shouldUpdatePrice_whenPriceProvided() {
        BigDecimal originalPrice = testProduct.getPrice();
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        Mockito.when(productRepository.save(testProduct)).thenReturn(testProduct);
        Product product = productService.updateProduct(1L, updateCommand);
        Assertions.assertEquals(0,product.getPrice().compareTo(updateCommand.price()));
        Assertions.assertNotEquals(0,product.getPrice().compareTo(originalPrice));
    }
    @Test
    public void updateProduct_shouldNotUpdatePrice_whenPriceNotProvided() {
        BigDecimal originalPrice = testProduct.getPrice();
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        Mockito.when(productRepository.save(testProduct)).thenReturn(testProduct);
        Product product = productService.updateProduct(1L, new UpdateProductCommand(null));
        Assertions.assertEquals(0,product.getPrice().compareTo(originalPrice));
    }
    @Test
    public void deleteProduct_shouldReturnException_whenProductDoesNotExist() {
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    }
    @Test
    public void deleteProduct_shouldDeleteProduct_whenProductExists() {
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        productService.deleteProduct(1L);
        Mockito.verify(productRepository, Mockito.times(1)).delete(testProduct);
    }
}
