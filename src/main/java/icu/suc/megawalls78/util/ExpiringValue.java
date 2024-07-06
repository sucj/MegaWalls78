package icu.suc.megawalls78.util;

import java.util.Objects;

public class ExpiringValue<V> {

  private final V value;
  private final long expired;

  public ExpiringValue(V value, long duration) {
    this.value = value;
    this.expired = System.currentTimeMillis() + duration;
  }

  public V getValue() {
    return value;
  }

  public boolean isExpired() {
    return System.currentTimeMillis() > expired;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExpiringValue<?> that = (ExpiringValue<?>) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
