package io.github.matheusfm;

import io.github.matheusfm.interfaces.ProductCacheRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;

public class ProductCacheRepositoryInMemory implements ProductCacheRepository {

  private static Map<String, Product> DATA = new HashMap<>();

  @Override
  public void save(@NonNull Product entity) {
    DATA.put(entity.getIdOrGenerate(), entity);
  }

  @Override
  public Optional<Product> findById(@NonNull String id) {
    return Optional.ofNullable(DATA.get(id));
  }

  @Override
  public List<Product> findAll() {
    return new ArrayList<>(DATA.values());
  }
}
