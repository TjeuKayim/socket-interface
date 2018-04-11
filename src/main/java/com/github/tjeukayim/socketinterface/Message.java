package com.github.tjeukayim.socketinterface;

public class Message {
  private final String endpoint;
  private final String method;
  private Object[] arguments;

  public Message(String endpoint, String method, Object... arguments) {
    this.endpoint = endpoint;
    this.method = method;
    this.arguments = arguments;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getMethod() {
    return method;
  }

  public Object[] getArguments() {
    return arguments;
  }

  public void setArguments(Object[] arguments) {
    this.arguments = arguments;
  }
}
