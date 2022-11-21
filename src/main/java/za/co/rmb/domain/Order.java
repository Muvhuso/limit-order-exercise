package za.co.rmb.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Order {
    private UUID id;
    private double price;
    private int quantity;
    private Direction side;
    private LocalDateTime dateTime;
}
