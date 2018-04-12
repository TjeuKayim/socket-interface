package com.github.tjeukayim.socketinterface;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

public class InvalidProtocolTest {

  private Consumer<SocketMessage> consumerMock = m -> {
  };

  @Test
  void noInterface() {
    assertThrows(IllegalArgumentException.class, () ->
        MessageSender.create(String.class, consumerMock));
  }

  interface A {

    void hello();
  }

  @Test
  void returnVoid() {
    assertThrows(IllegalArgumentException.class, () ->
        MessageSender.create(A.class, consumerMock));
  }

  interface B {

    C c();
  }

  interface C {

    boolean hello();
  }

  @Test
  void invalidEndpoint() {
    assertThrows(IllegalArgumentException.class, () ->
        MessageSender.create(B.class, consumerMock));
  }
}
