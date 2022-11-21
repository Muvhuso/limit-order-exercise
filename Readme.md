# Limit Order System

### Data structure chosen,TreeMap<OrderItemKey, LinkedList<>>:

- The data structure was chosen because it provides the capability to sort elements
with a comparator. In this specific scenario the key is 'OrderItemKey'
- Implementation of OrderItemKey is below:
####
```
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
```
- The above class provides the ability to sort on price and date.
- When an order is modified the date changes causing this item to lose priority
- A linkedList was chosen here to keep orders in the manner they were added
