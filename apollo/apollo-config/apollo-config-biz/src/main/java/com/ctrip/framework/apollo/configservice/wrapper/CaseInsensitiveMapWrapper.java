package com.ctrip.framework.apollo.configservice.wrapper;

import java.util.Map;

/**
 *  大小写 不敏感的Map
 * @param <T>
 */
public class CaseInsensitiveMapWrapper<T> {
  private final Map<String, T> delegate;

  public CaseInsensitiveMapWrapper(Map<String, T> delegate) {
    this.delegate = delegate;
  }

  public T get(String key) {
    return delegate.get(key.toLowerCase());
  }

  public T put(String key, T value) {
    return delegate.put(key.toLowerCase(), value);
  }

  public T remove(String key) {
    return delegate.remove(key.toLowerCase());
  }
}
