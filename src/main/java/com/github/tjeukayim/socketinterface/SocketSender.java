package com.github.tjeukayim.socketinterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Implements an interface and proxy invocations to a messageConsumer
 */
public class SocketSender {
  private final HashMap<String, Object> endpoints = new HashMap<>();
  private final Class clazz;
  private final Consumer<SocketMessage> messageConsumer;
  private static final Method OBJECT_EQUALS = getObjectMethod("equals", Object.class);
  private static final Method OBJECT_HASHCODE = getObjectMethod("hashCode");

  private SocketSender(Class clazz, Consumer<SocketMessage> messageConsumer) {
    this.clazz = clazz;
    this.messageConsumer = messageConsumer;
    createImplementations();
  }

  @SuppressWarnings("unchecked")
  public static <T> T create(Class<T> clazz, Consumer<SocketMessage> messageConsumer) {
    SocketSender sender = new SocketSender(clazz, messageConsumer);
    return (T) sender.getProxy();
  }

  /**
   * Each method of the interface clazz should have no arguments, and return another interface
   */
  private void createImplementations() {
    if (!clazz.isInterface()) {
      throw new IllegalArgumentException("clazz should be an interface");
    }
    for (Method method : clazz.getMethods()) {
      Class<?> returnType = method.getReturnType();
      if (method.getParameterCount() != 0) {
        throw new IllegalArgumentException("clazz methods shouldn't have arguments");
      }
      validateEndpoint(returnType);
      String endpoint = method.getName();
      Object implementation = interfaceProxy(returnType,
          (o, m, args) -> invocationHandler(endpoint, m, args));
      endpoints.put(endpoint, implementation);
    }
  }

  private Object invocationHandler(String endpoint, Method method, Object[] args) {
    // Send message
    SocketMessage message = new SocketMessage(endpoint, method.getName(), args);
    messageConsumer.accept(message);
    return null;
  }

  private Object getProxy() {
    return interfaceProxy(clazz, (proxy, method, args) ->
        endpoints.get(method.getName()));
  }

  private void validateEndpoint(Class endpoint) {
    if (!endpoint.isInterface()) {
      throw new IllegalArgumentException("clazz methods should return an interface");
    }
    for (Method method : endpoint.getMethods()) {
      if (!method.getReturnType().equals(Void.TYPE)) {
        throw new IllegalArgumentException("return type should be void");
      }
    }
  }

  private Object interfaceProxy(Class c, InvocationHandler h) {
    return Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, (proxy, method, args) -> {
      if (OBJECT_HASHCODE.equals(method)) {
        return hashCode();
      }
      if (OBJECT_EQUALS.equals(method)) {
        return args[0] == proxy;
      }
      return h.invoke(proxy, method, args);
    });
  }

  /**
   * Get a method-object from `Object`.
   * <p>
   * Source: https://javaclippings.wordpress.com/2009/03/18/dynamic-proxies-equals-hashcode-tostring/
   * </p>
   */
  private static Method getObjectMethod(String name, Class... types) {
    try {
      return Object.class.getMethod(name, types);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
