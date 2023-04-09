package ru.scrait.contactlesspayment.models;

import java.util.Date;


public class Ticket {
    public String email, date;
    public int sum, amount;

    public Ticket() {

    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public int getSum() {
        return sum;
    }

    public int getAmount() {
        return amount;
    }

    public Ticket(String email, String date, int sum, int amount) {
        this.email = email;
        this.sum = sum;
        this.amount= amount;
        this.date = date;
    }
}
