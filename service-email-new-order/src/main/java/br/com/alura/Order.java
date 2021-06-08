package br.com.alura;


import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Order {

    private final String orderId;
    private final BigDecimal value;
    private final String email;

    public Order(String orderId,
                 BigDecimal value,
                 String email) {
        this.orderId = orderId;
        this.value = value;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Order{" +
                "value=" + value +
                ", email='" + email + '\'' +
                '}';
    }
}
