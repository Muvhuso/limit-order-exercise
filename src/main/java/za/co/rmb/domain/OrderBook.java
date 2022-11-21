package za.co.rmb.domain;

import java.time.LocalDateTime;
import java.util.*;

public class OrderBook {

    private Map<OrderItemKey, LinkedList<Order>> buyMap;

    public OrderBook() {
        Comparator<OrderItemKey> orderItemKeyComparator = Comparator
                .comparing(OrderItemKey::getPrice)
                .thenComparing(OrderItemKey::getDateTime).reversed();
        buyMap = new TreeMap<>(orderItemKeyComparator);
    }

    public Order addOrder(Order order) {
        LocalDateTime now = LocalDateTime.now();
        order.setDateTime(now);
        OrderItemKey key = new OrderItemKey(order.getPrice(), now);
        if (!buyMap.containsKey(key)) {
            LinkedList<Order> orders = new LinkedList<>();
            order.setId(UUID.randomUUID());
            orders.add(order);
            buyMap.put(new OrderItemKey(order.getPrice(), now), orders);
        }
        return order;
    }

    public Order findByOrderId(UUID orderId) {
        Optional<LinkedList<Order>> optionalOrder = buyMap.values()
                .stream()
                .filter(o -> orderExistsInList(o, orderId)).findFirst();

        if (optionalOrder.isPresent()) {
            List<Order> orders = optionalOrder.get();
            if (orders.isEmpty()) {
                return null;
            }
            return optionalOrder.map(o -> o.stream()
                            .filter(f -> f.getId().equals(orderId))
                            .findFirst().get())
                    .orElse(null);

        }
        return null;
    }

    private boolean orderExistsInList(List<Order> orders, UUID orderId) {
        return orders.stream().anyMatch(f -> f.getId().equals(orderId));
    }
}
