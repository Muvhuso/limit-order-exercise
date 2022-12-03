package za.co.rmb.domain;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class OrderBook {
    private final Map<Double, List<Order>> bidMap;
    private final Map<Double, List<Order>> askMap;
    private IMatchOrders matchOrders;

    public OrderBook(IMatchOrders matchOrders) {
        this.matchOrders = matchOrders;
        bidMap = new TreeMap<>(Collections.reverseOrder());
        askMap = new TreeMap<>(Collections.reverseOrder());
    }

    public Order addOrder(Order order) {
        if (order.getSide() == Direction.Sell) {
            addOrder(order, askMap);
        } else {
            addOrder(order, bidMap);
        }

        return order;
    }

    private static void addOrder(Order order, Map<Double, List<Order>> map) {
        LocalDateTime now = LocalDateTime.now();
        order.setDateTime(now);
        order.setId(UUID.randomUUID());
        if (!map.containsKey(order.getPrice())) {
            LinkedList<Order> orders = new LinkedList<>();
            orders.add(order);
            map.put(order.getPrice(), orders);
        } else {
            List<Order> orders = map.get(order.getPrice());
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

    private static void sortList(List<Order> orders) {
        if (orders == null)
            return;
        orders.sort(Comparator.comparing(Order::getDateTime));
    }

    public void deleteOrder(UUID orderId) {
        Map<Double, List<Order>> askMapResponse = checkIfMapContainsOrder(orderId, askMap);
        if (askMapResponse.isEmpty() == false) {
            removeElementFromMap(orderId, askMapResponse);
        } else {
            Map<Double, List<Order>> bidMapResponse = checkIfMapContainsOrder(orderId, bidMap);
            removeElementFromMap(orderId, bidMapResponse);
        }
    }

    private Map<Double, List<Order>> checkIfMapContainsOrder(UUID orderId, Map<Double, List<Order>> map) {
        return map.entrySet()
                .stream()
                .filter(entry -> currentListContainsOrder(entry.getValue(), orderId))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    private static void removeElementFromMap(UUID orderId, Map<Double, List<Order>> map) {
        if (!map.isEmpty()) {
            map.values().stream().forEach(orders -> {
                orders.removeIf(order -> order.getId().equals(orderId));
            });
        }
    }

    private boolean currentListContainsOrder(List<Order> entries, UUID orderId) {
        return entries
                .stream()
                .anyMatch(item -> item.getId().equals(orderId));
    }

    public FindOrderResponse findByOrderId(UUID orderId) {
        Order askOrder = findOrder(this.askMap, orderId);
        if (askOrder != null) {
            return FindOrderResponse.orderResult(askOrder);
        }
        Order bidOrder = findOrder(this.bidMap, orderId);
        if (bidOrder != null) {
            return FindOrderResponse.orderResult(bidOrder);
        }

        return FindOrderResponse.orderNotFound();
    }

    private Order findOrder(Map<Double, List<Order>> map, UUID orderId) {
        Optional<List<Order>> optionalOrder = map.values()
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

    public List<Order> findOrderByPriceAndDirection(Direction side, Double price) {
        if (side == Direction.Buy) {
            List<Order> orders = bidMap.get(price);
            sortList(orders);
            return orders;
        }

        if (side == Direction.Sell) {
            List<Order> orders = askMap.get(price);
            sortList(orders);
            return orders;
        }

        return null;
    }

    public MatchOperationResponse match() {
        return matchOrders.matchOrders(askMap, bidMap);
    }
}
