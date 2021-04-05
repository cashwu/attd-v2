package com.odde.atddv2.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String code, productName, recipientName, recipientMobile, recipientAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal total;

    public enum OrderStatus {
        toBeDelivered
    }
}
