package io.github.matheusfm;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Product {

  private String id;
  private final String name;
  private final Integer partnerId;

  public void generateId() {
    if (!hasId()) {
      this.id = partnerId + "-" + UUID.randomUUID().toString();
    }
  }

  public boolean hasId() {
    return id != null;
  }

  public String getIdOrGenerate() {
    generateId();
    return this.id;
  }
}
