package com.github.tjeukayim.socketinterface;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public final class SenderReceiverTest {

  @Test
  void sendHello() {
    ProtocolMock protocolMock = new ProtocolMock();
    MessageReceiver<ProtocolMock> receiver = new MessageReceiver<>(protocolMock);
    Protocol sender = MessageSender.create(Protocol.class, receiver::receive);
    sender.hello();
  }

  static class ProtocolMock implements Protocol {

    @Override
    public void hello() {
      System.out.println("hello");
    }

    @Override
    public void chat(String message) {

    }

    @Override
    public void login(Login f) {

    }
  }
}
