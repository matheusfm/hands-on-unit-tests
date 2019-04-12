package io.github.matheusfm.interfaces;

import java.util.List;
import java.util.Optional;

interface Repository<E, ID> {

  void save(E entity);

  Optional<E> findById(ID id);

  List<E> findAll();

  void delete(E entity);
}
