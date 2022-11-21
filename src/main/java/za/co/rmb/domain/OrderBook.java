package za.co.rmb.domain;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class OrderBook {
    private Map<Double, LinkedList<Order>> buyMap;
    private Map<Double, LinkedList<Order>> sellMap;

    public OrderBook() {

        buyMap = new TreeMap<>(Collections.reverseOrder());
        sellMap = new TreeMap<>(Collections.reverseOrder());
    }

    public Order addOrder(Order order) {
        if (order.getSide() == Direction.Sell) {
            addOrder(order, sellMap);
        } else {
            addOrder(order, buyMap);
        }

        return order;
    }

    private static void addOrder(Order order, Map<Double, LinkedList<Order>> map) {
        LocalDateTime now = LocalDateTime.now();
        order.setDateTime(now);
        order.setId(UUID.randomUUID());
        if (!map.containsKey(order.getPrice())) {
            LinkedList<Order> orders = new LinkedList<>();
            orders.add(order);
            map.put(order.getPrice(), orders);
        } else {
            LinkedList<Order> orders = map.get(order.getPrice());
            orders.add(order);
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
        Map<Double, LinkedList<Order>> sellMapResponse = checkIfMapContainsOrder(orderId, sellMap);
        if (sellMapResponse.isEmpty() == false) {
            removeElementFromMap(orderId, sellMapResponse);
        } else {
            Map<Double, LinkedList<Order>> buyMapResponse = checkIfMapContainsOrder(orderId, buyMap);
            removeElementFromMap(orderId, buyMapResponse);
        }
    }

    private Map<Double, LinkedList<Order>> checkIfMapContainsOrder(UUID orderId, Map<Double, LinkedList<Order>> map) {
        return map.entrySet()
                .stream()
                .filter(entry -> currentListContainsOrder(entry.getValue(), orderId))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    private static void removeElementFromMap(UUID orderId, Map<Double, LinkedList<Order>> map) {
        if (!map.isEmpty()) {
            map.values().stream().forEach(orders -> {
                orders.removeIf(order -> order.getId().equals(orderId));
            });
        }
    }

    private boolean currentListContainsOrder(LinkedList<Order> entries, UUID orderId) {
        return entries
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

    private Order findOrder(Map<Double, LinkedList<Order>> map, UUID orderId) {
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

    public LinkedList<Order> findOrderByPriceAndDirection(Direction side, Double price) {
        if (side == Direction.Buy) {
            return sellMap.get(price);
        }

        if (side == Direction.Sell) {
            return sellMap.get(price);
        }

        return null;
    }
}
