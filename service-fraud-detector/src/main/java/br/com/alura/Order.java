package br.com.alura;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Order {

    private final String userId;
    private final String orderId;
    private final BigDecimal value;

    public Order(String userId,
                 String orderId,
                 BigDecimal value) {
        this.userId = userId;
        this.orderId = orderId;
        this.value = value;
    }
}
