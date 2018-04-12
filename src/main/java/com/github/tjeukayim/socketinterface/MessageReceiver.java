package com.github.tjeukayim.socketinterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.Function;

public class MessageReceiver {

  private final HashMap<String, Endpoint> endpoints = new HashMap<>();
  private final Object target;

  public MessageReceiver(Class<?> clazz, Object target) {
    this.target = target;
    Method[] declaredMethods = clazz.getMethods();
    for (Method method : declaredMethods) {
      if (method.getParameterCount() != 0) {
        throw new IllegalArgumentException("Methods should have no arguments");
      }

      endpoints.put(method.getName(), new Endpoint(method));
    }
  }

  public void receive(Message message) {
    receive(message, null);
  }

  public void receive(Message message, Function<Class<?>[], Object[]> argumentParser) {
    Endpoint endpoint = endpoints.get(message.getEndpoint());
    if (endpoint == null) {
      throw new IllegalArgumentException("endpoint does not exist");
    }
    String method = message.getMethod();
    Class<?>[] parameterTypes = endpoint.getParameterTypes(method);
    if (parameterTypes == null) {
      throw new IllegalArgumentException("method does not exist");
    }
    Object[] arguments = message.getArguments();
    if (argumentParser != null) {
      arguments = argumentParser.apply(parameterTypes);
    }
    try {
      // Get implementation of endpoint, and invoke the requested method
      Object implementation = endpoint.getFactory().invoke(target, (Object[]) null);
      implementation.getClass()
          .getMethod(method, parameterTypes)
          .invoke(implementation, arguments);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalArgumentException("invocation failed");
    } catch (NullPointerException e) {
      throw new IllegalStateException("call to " + message.getEndpoint() + " returned null");
    }
  }

  private static class Endpoint {

    final HashMap<String, Class<?>[]> parameterTypes = new HashMap<>();
    private final Method factory;

    Endpoint(Method factory) {
      this.factory = factory;
      for (Method method : factory.getReturnType().getMethods()) {
        parameterTypes.put(method.getName(), method.getParameterTypes());
      }
    }

    Class<?>[] getParameterTypes(String name) {
      return parameterTypes.get(name);
    }

    Method getFactory() {
      return factory;
    }
  }
}
