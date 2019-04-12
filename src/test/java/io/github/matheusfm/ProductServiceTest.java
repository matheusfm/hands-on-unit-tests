package io.github.matheusfm;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import io.github.matheusfm.interfaces.ProductCacheRepository;
import io.github.matheusfm.interfaces.ProductPersistentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

  @InjectMocks
  private ProductService productService;

  @Mock
  private ProductCacheRepository cacheRepository;
  @Mock
  private ProductPersistentRepository persistentRepository;

  @Captor
  private ArgumentCaptor<Product> argumentCaptor;

  @Test
  public void saveTest() {
    final Product product = new Product("Notebook", 1);

    productService.save(product);

    verify(persistentRepository).save(product);
    verify(cacheRepository).save(product);
  }

  @Test
  public void saveWithIdTest() {
    final Product product = new Product("Notebook", 1);

    productService.save(product);

    verify(cacheRepository, times(1)).save(argumentCaptor.capture());
    assertNotNull(argumentCaptor.getValue());
    assertNotNull(argumentCaptor.getValue().getId());
    assertTrue(argumentCaptor.getValue().getId().matches("1-.*"));
    assertEquals("Notebook", argumentCaptor.getValue().getName());
    assertEquals(Integer.valueOf(1), product.getPartnerId());
  }

  @Test
  public void findCachedProductTest() {
    final String id = UUID.randomUUID().toString();

    when(cacheRepository.findById(anyString()))
        .thenReturn(Optional.of(new Product(id, "Notebook", 1)));

    Product product = productService.findById(id);

    verify(cacheRepository).findById(id);
    verify(persistentRepository, never()).findById(id);
    assertNotNull(product);
    assertEquals(id, product.getId());
    assertEquals("Notebook", product.getName());
    assertEquals(Integer.valueOf(1), product.getPartnerId());
  }

  @Test
  public void findNonCachedProductTest() {
    final String id = UUID.randomUUID().toString();

    doReturn(Optional.empty()).when(cacheRepository).findById(eq(id));
    when(persistentRepository.findById(any()))
        .thenReturn(Optional.of(new Product(id, "Notebook", 1)));

    Product product = productService.findById(id);

    verify(cacheRepository).findById(id);
    verify(persistentRepository).findById(id);
    assertNotNull(product);
    assertEquals(id, product.getId());
    assertEquals("Notebook", product.getName());
    assertEquals(Integer.valueOf(1), product.getPartnerId());
  }

  @Test
  public void unavailableRepositoryTest() {
    final String id = UUID.randomUUID().toString();

    when(cacheRepository.findById(any(String.class)))
        .thenThrow(RuntimeException.class);
    when(persistentRepository.findById(anyString()))
        .thenReturn(Optional.of(new Product(id, "Notebook", 1)));

    Product product = productService.findById(id);

    verify(cacheRepository).findById(id);
    verify(persistentRepository).findById(id);
    assertNotNull(product);
    assertEquals(id, product.getId());
    assertEquals("Notebook", product.getName());
    assertEquals(Integer.valueOf(1), product.getPartnerId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalArgumentFindTest() {
    productService.findById(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalArgumentSaveTest() {
    productService.save(null);
  }

  @Test
  public void findAllNonCachedTest() {
    when(cacheRepository.findAll()).thenReturn(new ArrayList<>());
    when(persistentRepository.findAll()).thenReturn(asList(
        new Product("Notebook", 1),
        new Product("Smartphone", 1)));

    final List<Product> products = productService.findAll();

    verify(cacheRepository).findAll();
    verify(persistentRepository).findAll();
    assertNotNull(products);
    assertFalse(products.isEmpty());
    assertEquals(2, products.size());
  }

  @Test
  public void findAllCachedTest() {
    when(cacheRepository.findAll()).thenReturn(asList(
        new Product("Notebook", 1),
        new Product("Smartphone", 1)));

    final List<Product> products = productService.findAll();

    verify(cacheRepository).findAll();
    verifyZeroInteractions(persistentRepository);
    assertNotNull(products);
    assertFalse(products.isEmpty());
    assertEquals(2, products.size());
  }

}