package com.github.tjeukayim.socketinterface;

import com.google.gson.Gson;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;

public final class MessageSender {

  private MessageSender() {
  }

  public static <T> T create(Class<T> clazz, Consumer<String> consumer) {
    Gson gson = new Gson();

    //Proxy.getInvocationHandler()
  }
}
