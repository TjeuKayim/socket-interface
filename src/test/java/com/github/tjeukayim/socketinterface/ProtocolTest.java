package com.github.tjeukayim.socketinterface;

import static org.junit.jupiter.api.Assertions.*;

import com.github.tjeukayim.socketinterface.Account.Login;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class ProtocolTest {

  private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
  private ProtocolMock protocolMock;
  private Protocol sender;
  private MessageReceiver receiver;

  private Protocol createSender(Protocol protocol) {
    receiver = new MessageReceiver(Protocol.class, protocol);
    return MessageSender.create(Protocol.class, print(receiver::receive));
  }

  @BeforeEach
  void setUp() {
    protocolMock = new ProtocolMock();
    sender = createSender(protocolMock);
  }

  @Test
  void noArguments() {
    sender.account().logout();
    assertTrue(protocolMock.loggedOut);
  }

  @Test
  void multipleArguments() {
    sender.chat().chat("Hello World", true);
    assertEquals("Hello World", protocolMock.message);
    assertEquals(true, protocolMock.bool);
  }

  @Test
  void complexArgument() {
    Login login = new Login(3, "Henkie", "myPassword");
    sender.account().login(login);
    assertEquals(login, protocolMock.login);
  }

  @Test
  void argumentParser() {
    SocketMessage message = new SocketMessage("chat", "chat", (Object[]) null);
    receiver.receive(message, types -> {
      assertArrayEquals(types, new Class[] {String.class, Boolean.TYPE});
      return new Object[] {"Hello World", true};
    });
    assertEquals("Hello World", protocolMock.message);
    assertEquals(true, protocolMock.bool);
  }

  @Test
  void invalidMessage() {
    SocketMessage m = new SocketMessage("this", "does", "not", "exist");
    assertThrows(IllegalArgumentException.class, () -> receiver.receive(m));
  }

  @Test
  void missingEndpoint() {
    protocolMock.account = null;
    assertThrows(IllegalStateException.class, () -> sender.account().logout());
  }

  private <T> Consumer<T> print(Consumer<T> consumer) {
    return (T t) -> {
      System.out.println(gson.toJson(t));
      consumer.accept(t);
    };
  }

  class ProtocolMock implements Protocol {

    String message;
    Boolean bool;
    boolean loggedOut = false;
    Login login;
    private Account account;

    ProtocolMock() {
      account = new Account() {
        @Override
        public void logout() {
          loggedOut = true;
        }

        @Override
        public void login(Login f) {
          login = f;
        }
      };
    }

    @Override
    public Chat chat() {
      return (m, b) -> {
        message = m;
        bool = b;
      };
    }

    @Override
    public Account account() {
      return account;
    }
  }
}
