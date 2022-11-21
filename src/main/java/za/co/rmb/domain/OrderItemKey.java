package za.co.rmb.domain;

import java.time.LocalDateTime;

public class OrderItemKey {
    private double price;
    private LocalDateTime dateTime;

    public OrderItemKey(double price, LocalDateTime dateTime) {
        this.price = price;
        this.dateTime = dateTime;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
