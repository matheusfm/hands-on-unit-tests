package io.github.matheusfm;

import io.github.matheusfm.interfaces.CacheRepository;
import io.github.matheusfm.interfaces.PersistentRepository;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public class ProductService {

  private final CacheRepository cacheRepository;
  private final PersistentRepository persistentRepository;

  public ProductService(CacheRepository cacheRepository,
      PersistentRepository persistentRepository) {
    this.cacheRepository = cacheRepository;
    this.persistentRepository = persistentRepository;
  }

  public void save(@NonNull Product product) {
    product.generateId();
    persistentRepository.save(product);
    cacheRepository.save(product);
  }

  public Product findProductById(@NonNull String id) {
    Optional<Product> product;
    try {
      product = cacheRepository.findById(id);
    } catch (RuntimeException ex) {
      product = Optional.empty();
    }

    return product.orElseGet(() -> persistentRepository.findById(id).orElse(null));
  }

  public List<Product> findAll() {
    return Optional.of(cacheRepository.findAll())
        .filter(products -> !products.isEmpty())
        .orElseGet(persistentRepository::findAll);
  }

}
