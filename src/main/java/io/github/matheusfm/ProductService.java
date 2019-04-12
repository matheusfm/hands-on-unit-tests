package io.github.matheusfm;

import io.github.matheusfm.interfaces.ProductCacheRepository;
import io.github.matheusfm.interfaces.ProductPersistentRepository;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public class ProductService {

  private final ProductCacheRepository cacheRepository;
  private final ProductPersistentRepository persistentRepository;

  public ProductService(ProductCacheRepository cacheRepository,
      ProductPersistentRepository persistentRepository) {

    this.cacheRepository = cacheRepository;
    this.persistentRepository = persistentRepository;
  }

  public void save(@NonNull Product product) {
    product.generateId();
    persistentRepository.save(product);
    cacheRepository.save(product);
  }

  public Product findById(@NonNull String id) {
    Optional<Product> product;
    try {
      product = cacheRepository.findById(id);
    } catch (RuntimeException ex) {
      product = Optional.empty();
    }

    return product.orElseGet(() -> persistentRepository.findById(id).orElse(null));
  }

  public List<Product> findAll() {
    return Optional.ofNullable(cacheRepository.findAll())
        .filter(products -> !products.isEmpty())
        .orElseGet(persistentRepository::findAll);
  }

  public void delete(@NonNull Product product) {
    persistentRepository.delete(product);
    cacheRepository.delete(product);
  }

}
