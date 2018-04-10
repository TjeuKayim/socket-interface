package com.github.tjeukayim.socketinterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MessageReceiver {

  private final HashMap<String, Endpoint> endpoints = new HashMap<>();
  private final Object target;

  public MessageReceiver(Object target) {
    this.target = target;
    Class clazz = target.getClass();
    Method[] declaredMethods = clazz.getMethods();
    for (Method method : declaredMethods) {
      if (method.getParameterCount() != 0) {
        throw new IllegalArgumentException("Methods should have no arguments");
      }

      endpoints.put(method.getName(), new Endpoint(method));
    }
  }

  public void receive(Message message) {
    Endpoint endpoint = endpoints.get(message.getEndpoint());
    if (endpoint == null) {
      throw new IllegalArgumentException("endpoint does not exist");
    }
    Class<?>[] parameterTypes = endpoint.getParameterTypes(message.getMethod());
    if (parameterTypes == null) {
      throw new IllegalArgumentException("method does not exist");
    }
    try {
      // Get implementation of endpoint, and invoke the requested method
      Object implementation = endpoint.getFactory().invoke(target, (Object[]) null);
      implementation.getClass()
          .getMethod(message.getMethod(), parameterTypes)
          .invoke(implementation, message.getArguments());
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalArgumentException("invocation failed");
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
