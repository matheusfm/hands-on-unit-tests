package io.github.matheusfm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import io.github.matheusfm.interfaces.ProductCacheRepository;
import io.github.matheusfm.interfaces.ProductPersistentRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceInMemoryTest {

  @InjectMocks
  private ProductService productService;

  @Spy
  private ProductCacheRepository cacheRepository = new ProductInMemoryRepository();
  @Mock
  private ProductPersistentRepository persistentRepository;

  @Test
  public void inMemoryTestWithoutStubbing() {
    final String id = UUID.randomUUID().toString();

    productService.save(new Product(id, "Notebook", 1));
    final Product product = productService.findById(id);

    assertNotNull(product);
    assertEquals(id, product.getId());

    productService.delete(product);
  }

  @Test
  public void inMemoryRepositoryNonSavingTest() {
    final String id = UUID.randomUUID().toString();

    doNothing().when(cacheRepository).save(any(Product.class));

    productService.save(new Product(id, "Notebook", 1));
    final Product product = productService.findById(id);

    assertNull(product);
  }

  @Test
  public void inMemoryRepositoryNonFindingTest() {
    final String id = UUID.randomUUID().toString();

    when(cacheRepository.findById(id)).thenReturn(Optional.empty());
    when(persistentRepository.findById(id)).thenReturn(Optional.of(new Product(id, "Notebook", 1)));

    productService.save(new Product(id, "Notebook", 1));
    final Product product = productService.findById(id);

    assertNotNull(product);
    assertEquals(id, product.getId());
  }
}
