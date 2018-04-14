package com.github.tjeukayim;

import static org.junit.jupiter.api.Assertions.*;

import com.github.tjeukayim.socketinterface.SocketMessage;
import com.github.tjeukayim.socketinterface.SocketSender;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

public class InvalidProtocolTest {

  private Consumer<SocketMessage> consumerMock = m -> {
  };

  @Test
  void noInterface() {
    assertThrows(IllegalArgumentException.class, () ->
        SocketSender.create(String.class, consumerMock));
  }

  interface A {

    void hello();
  }

  @Test
  void returnVoid() {
    assertThrows(IllegalArgumentException.class, () ->
        SocketSender.create(A.class, consumerMock));
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
        SocketSender.create(B.class, consumerMock));
  }
}
