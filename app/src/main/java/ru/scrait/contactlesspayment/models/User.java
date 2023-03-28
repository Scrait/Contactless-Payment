package ru.scrait.contactlesspayment.models;

public class User {
    public String email, password;
    public int balance;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.balance = 0;
    }
}
