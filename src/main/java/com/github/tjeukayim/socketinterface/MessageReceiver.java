package com.github.tjeukayim.socketinterface;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

public class MessageReceiver {

  private final HashMap<String, Class> interfaces = new HashMap<>();

  public MessageReceiver(Class clazz) {
    Method[] declaredMethods = clazz.getDeclaredMethods();
    for (Method method : declaredMethods) {
      if (method.getParameterCount() != 0) {
        throw new IllegalArgumentException("Methods should have no parameters");
      }
      Class<?> returnType = method.getReturnType();
      interfaces.put(method.getName(), returnType);
    }
  }

  public void receive(String message) {

  }

  public <T> MessageReceiver add(T target) {
    Optional<String> name = interfaces.entrySet()
        .stream()
        .filter(t -> t.getValue().isInstance(target))
        .map(Entry::getKey)
        .findAny();
    if (!name.isPresent()) {
      throw new IllegalArgumentException("Cannot find interface");
    }

  }

  private static class TargetClass<T> {

    final Class<T> clazz;
    final T target;

    TargetClass(Class<T> clazz, T target) {
      this.clazz = clazz;
      this.target = target;
    }
  }
}
