package com.github.tjeukayim.socketinterface;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.function.Consumer;

public final class MessageSender {

  private static final Gson gson = new Gson();
  private final HashMap<String, Object> implementations = new HashMap<>();
  private final Class clazz;
  private final Consumer<String> consumer;

  private MessageSender(Class clazz, Consumer<String> consumer) {
    this.clazz = clazz;
    this.consumer = consumer;
    createImplementations();
  }

  @SuppressWarnings("unchecked")
  public static <T> T create(Class<T> clazz, Consumer<String> consumer) {
    MessageSender messageSender = new MessageSender(clazz, consumer);
    return (T) messageSender.getProxy();
  }

  private static Object interfaceProxy(Class c, InvocationHandler h) {
    return Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, h);
  }

  /**
   * Each method of the interface clazz should have no arguments, and return another interface
   */
  private void createImplementations() {
    if (!clazz.isInterface()) {
      throw new IllegalArgumentException("clazz should be an interface");
    }
    for (Method method : clazz.getDeclaredMethods()) {
      Class<?> returnType = method.getReturnType();
      if (!returnType.isInterface()) {
        throw new IllegalArgumentException("clazz methods should return an interface");
      }
      if (method.getParameterCount() != 0) {
        throw new IllegalArgumentException("clazz methods shouldn't have parameters");
      }
      String interfaceName = method.getName();
      Object implementation = interfaceProxy(returnType,
          (o, m, args) -> invocationHandler(interfaceName, m, args));
      implementations.put(interfaceName, implementation);
    }
  }

  private Object invocationHandler(String interfaceName, Method method, Object[] args) {
    if (!method.getReturnType().equals(Void.TYPE)) {
      throw new UnsupportedOperationException("return type should be void");
    }
    // Send message
    String name = interfaceName + "/" + method.getName();
    JsonArray message = new JsonArray();
    message.add(name);
    for (Object arg : args) {
      message.add(gson.toJsonTree(arg));
    }
    consumer.accept(gson.toJson(message));
    return null;
  }

  private Object getProxy() {
    return interfaceProxy(clazz, (proxy, method, args) ->
        implementations.get(method.getName()));
  }
}
