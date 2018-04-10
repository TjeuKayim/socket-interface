package com.github.tjeukayim.socketinterface;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public final class SenderReceiverTest {

  @Test
  void sender() {
    Protocol p = MessageSender.create(Protocol.class, System.out::println);
    p.chat().chat("Hello World", true);
  }

  @Test
  void chat() {
    ProtocolMock protocolMock = new ProtocolMock();
    MessageReceiver receiver = new MessageReceiver(protocolMock);
    Protocol sender = MessageSender.create(Protocol.class, receiver::receive);
  }

  static class ProtocolMock implements Protocol {

    @Override
    public Account account() {
      return new Account() {
        @Override
        public void login(Login f) {

        }

        @Override
        public void logout() {

        }
      };
    }

    @Override
    public Chat chat() {
      return (message, dateTime) -> {

      };
    }
  }
}
