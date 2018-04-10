package com.github.tjeukayim.socketinterface;

/**
 * The interface used in unit-tests
 */
public interface Protocol {
  void hello();

  void chat(String message);

  void login(Login f);

  /**
   * POJO with login information
   */
  class Login {
    private int id;
    private String name;
    private String password;

    public Login(int id, String name, String password) {
      this.id = id;
      this.name = name;
      this.password = password;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getPassword() {
      return password;
    }
  }
}
