package za.co.rmb.domain;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    public void modifyOrder(UUID orderId, int newQuantity) {
        FindOrderResponse orderResponse = findByOrderId(orderId);
        if (orderResponse.isOrderFound()) {
            Order order = orderResponse.getOrder();
            order.setQuantity(newQuantity);
            order.setDateTime(LocalDateTime.now());
        }
    }

    public void deleteOrder(UUID orderId) {
        Map<OrderItemKey, LinkedList<Order>> ordersFound = buyMap.entrySet()
                .stream()
                .filter(entry -> currentListContainsOrder(entry, orderId))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
        if (!ordersFound.isEmpty()) {
            ordersFound.values().stream().forEach(orders -> {
                orders.removeIf(order -> order.getId().equals(orderId));
            });
        }
    }

    private boolean currentListContainsOrder(Map.Entry<OrderItemKey, LinkedList<Order>> entry, UUID orderId) {
        return entry.getValue()
                .stream()
                .anyMatch(item -> item.getId().equals(orderId));
    }

    public FindOrderResponse findByOrderId(UUID orderId) {
        Optional<LinkedList<Order>> optionalOrder = buyMap.values()
                .stream()
                .filter(o -> orderExistsInList(o, orderId)).findFirst();

        if (optionalOrder.isPresent()) {
            List<Order> orders = optionalOrder.get();
            if (orders.isEmpty()) {
                return FindOrderResponse.orderNotFound();
            }
            Optional<Order> order = optionalOrder.map(o -> o.stream()
                    .filter(f -> f.getId().equals(orderId))
                    .findFirst().get());
            if (order.isPresent()) {
                return FindOrderResponse.orderResult(order.get());
            }

        }
        return FindOrderResponse.orderNotFound();
    }

    private boolean orderExistsInList(List<Order> orders, UUID orderId) {
        return orders.stream().anyMatch(f -> f.getId().equals(orderId));
    }
}
