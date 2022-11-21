package za.co.rmb.domain;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class OrderBook {
    private Map<OrderItemKey, LinkedList<Order>> buyMap;
    private Map<OrderItemKey, LinkedList<Order>> sellMap;

    public OrderBook() {
        Comparator<OrderItemKey> orderItemKeyComparator = Comparator
                .comparing(OrderItemKey::getPrice)
                .thenComparing(OrderItemKey::getDateTime).reversed();
        buyMap = new TreeMap<>(orderItemKeyComparator);
        sellMap = new TreeMap<>(orderItemKeyComparator);
    }

    public Order addOrder(Order order) {
        if (order.getSide() == Direction.Sell) {
            addOrder(order, sellMap);
        } else {
            addOrder(order, buyMap);
        }

        return order;
    }

    private static void addOrder(Order order, Map<OrderItemKey, LinkedList<Order>> map) {
        LocalDateTime now = LocalDateTime.now();
        order.setDateTime(now);
        OrderItemKey key = new OrderItemKey(order.getPrice(), now);
        if (!map.containsKey(key)) {
            LinkedList<Order> orders = new LinkedList<>();
            order.setId(UUID.randomUUID());
            orders.add(order);
            map.put(new OrderItemKey(order.getPrice(), now), orders);
        }
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
        Map<OrderItemKey, LinkedList<Order>> sellMapResponse = checkIfMapContainsOrder(orderId, sellMap);
        if (sellMapResponse.isEmpty() == false) {
            removeElementFromMap(orderId, sellMapResponse);
        } else {
            Map<OrderItemKey, LinkedList<Order>> buyMapResponse = checkIfMapContainsOrder(orderId, buyMap);
            removeElementFromMap(orderId, buyMapResponse);
        }
    }

    private Map<OrderItemKey, LinkedList<Order>> checkIfMapContainsOrder(UUID orderId, Map<OrderItemKey, LinkedList<Order>> map) {
        return map.entrySet()
                .stream()
                .filter(entry -> currentListContainsOrder(entry, orderId))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    private static void removeElementFromMap(UUID orderId, Map<OrderItemKey, LinkedList<Order>> map) {
        if (!map.isEmpty()) {
            map.values().stream().forEach(orders -> {
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
        Order sellOrder = findOrder(this.sellMap, orderId);
        if (sellOrder != null) {
            return FindOrderResponse.orderResult(sellOrder);
        }
        Order buyOrder = findOrder(this.buyMap, orderId);
        if (buyOrder != null) {
            return FindOrderResponse.orderResult(buyOrder);
        }

        return FindOrderResponse.orderNotFound();
    }

    private Order findOrder(Map<OrderItemKey, LinkedList<Order>> map, UUID orderId) {
        Optional<LinkedList<Order>> optionalOrder = map.values()
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
