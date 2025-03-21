package com.foodmanagement.foodmanagement.entity;

import java.io.Serializable;
import java.util.Objects;

public class OrderlineId implements Serializable {
    private int order; // Must match the name of the @Id field in Orderline
    private int food; // Must match the name of the @Id field in Orderline

    public OrderlineId() {
    }

    public OrderlineId(int order, int food) {
        this.order = order;
        this.food = food;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OrderlineId that = (OrderlineId) o;
        return order == that.order && food == that.food;
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, food);
    }
}