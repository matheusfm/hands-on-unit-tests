package io.github.matheusfm;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import io.github.matheusfm.interfaces.ProductCacheRepository;
import io.github.matheusfm.interfaces.ProductPersistentRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Introdução testes unitários e integrados JUnit e Mockito Mock e Spy Regras do exemplo Objetivo:
 * Cobertura; Quebrar testes; CI; Intercalar
 *
 * TODO: Spy and Annotations
 */
public class ProductServiceTest {

  private ProductService productService;

  private ProductCacheRepository cacheRepository;
  private ProductPersistentRepository persistentRepository;

  @Before
  public void setUp() {
    cacheRepository = Mockito.mock(ProductCacheRepository.class);
    persistentRepository = Mockito.mock(ProductPersistentRepository.class);

    productService = new ProductService(cacheRepository, persistentRepository);
  }

  @Test
  public void saveTest() {
    final Product product = new Product("Notebook", 1);

    productService.save(product);

    Mockito.verify(persistentRepository).save(product);
    Mockito.verify(cacheRepository).save(product);
  }

  @Test
  public void saveWithIdTest() {
    final Product product = new Product("Notebook", 1);

    productService.save(product);

    ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);

    Mockito.verify(cacheRepository, Mockito.times(1)).save(argumentCaptor.capture());
    Assert.assertNotNull(argumentCaptor.getValue());
    Assert.assertNotNull(argumentCaptor.getValue().getId());
    Assert.assertTrue(argumentCaptor.getValue().getId().matches("1-.*"));
    Assert.assertEquals("Notebook", argumentCaptor.getValue().getName());
    Assert.assertEquals(Integer.valueOf(1), product.getPartnerId());
  }

  @Test
  public void findCachedProductTest() {
    final String id = UUID.randomUUID().toString();

    Mockito.when(cacheRepository.findById(anyString()))
        .thenReturn(Optional.of(new Product(id, "Notebook", 1)));

    Product product = productService.findProductById(id);

    Mockito.verify(cacheRepository).findById(id);
    Mockito.verify(persistentRepository, Mockito.never()).findById(id);
    Assert.assertNotNull(product);
    Assert.assertEquals(id, product.getId());
    Assert.assertEquals("Notebook", product.getName());
    Assert.assertEquals(Integer.valueOf(1), product.getPartnerId());
  }

  @Test
  public void findNonCachedProductTest() {
    final String id = UUID.randomUUID().toString();

    Mockito.doReturn(Optional.empty()).when(cacheRepository).findById(eq(id));
    Mockito.when(persistentRepository.findById(any()))
        .thenReturn(Optional.of(new Product(id, "Notebook", 1)));

    Product product = productService.findProductById(id);

    Mockito.verify(cacheRepository).findById(id);
    Mockito.verify(persistentRepository).findById(id);
    Assert.assertNotNull(product);
    Assert.assertEquals(id, product.getId());
    Assert.assertEquals("Notebook", product.getName());
    Assert.assertEquals(Integer.valueOf(1), product.getPartnerId());
  }

  @Test
  public void unavailableRepositoryTest() {
    final String id = UUID.randomUUID().toString();

    Mockito.when(cacheRepository.findById(any(String.class)))
        .thenThrow(RuntimeException.class);
    Mockito.when(persistentRepository.findById(anyString()))
        .thenReturn(Optional.of(new Product(id, "Notebook", 1)));

    Product product = productService.findProductById(id);

    Mockito.verify(cacheRepository).findById(id);
    Mockito.verify(persistentRepository).findById(id);
    Assert.assertNotNull(product);
    Assert.assertEquals(id, product.getId());
    Assert.assertEquals("Notebook", product.getName());
    Assert.assertEquals(Integer.valueOf(1), product.getPartnerId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalArgumentFindTest() {
    productService.findProductById(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalArgumentSaveTest() {
    productService.save(null);
  }

  @Test
  public void findAllNonCachedTest() {
    Mockito.when(cacheRepository.findAll()).thenReturn(new ArrayList<>());
    Mockito.when(persistentRepository.findAll()).thenReturn(Arrays.asList(
        new Product("1-1", "Notebook", 1),
        new Product("2-1", "Smartphone", 1)));

    final List<Product> products = productService.findAll();

    Mockito.verify(cacheRepository).findAll();
    Mockito.verify(persistentRepository).findAll();
    Assert.assertNotNull(products);
    Assert.assertFalse(products.isEmpty());
    Assert.assertEquals(2, products.size());
  }

  @Test
  public void findAllCachedTest() {
    Mockito.when(cacheRepository.findAll()).thenReturn(Arrays.asList(
        new Product("1-1", "Notebook", 1),
        new Product("1-2", "Smartphone", 1)));

    final List<Product> products = productService.findAll();

    Mockito.verify(cacheRepository).findAll();
    Mockito.verifyZeroInteractions(persistentRepository);
    Assert.assertNotNull(products);
    Assert.assertFalse(products.isEmpty());
    Assert.assertEquals(2, products.size());
  }

}