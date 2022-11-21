package za.co.rmb.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Order {
    private UUID id;
    private double price;
    private int quantity;
    private Direction side;
    private LocalDateTime dateTime;

    public Order(double price, int quantity, Direction side) {
        this.price = price;
        this.quantity = quantity;
        this.side = side;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Direction getSide() {
        return side;
    }

    public void setSide(Direction side) {
        this.side = side;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
