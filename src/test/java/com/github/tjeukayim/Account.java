package com.github.tjeukayim;

import java.util.Objects;

public interface Account {
  void login(Login f);
  void logout();

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

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Login login = (Login) o;
      return id == login.id &&
          Objects.equals(name, login.name) &&
          Objects.equals(password, login.password);
    }

    @Override
    public int hashCode() {

      return Objects.hash(id, name, password);
    }
  }
}
