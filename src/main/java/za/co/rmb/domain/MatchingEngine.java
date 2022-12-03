package za.co.rmb.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MatchingEngine implements IMatchOrders {
    @Override
    public MatchOperationResponse matchOrders(Map<Double, List<Order>> askOrders, Map<Double, List<Order>> bidOrders) {
        if (bidOrders.isEmpty()) {
            return MatchOperationResponse.NoBidOrdersAvailable;
        }

        if (askOrders.isEmpty()) {
            return MatchOperationResponse.NoAskOrdersAvailable;
        }

        for (Map.Entry<Double, List<Order>> buyEntry : bidOrders.entrySet()) {
            Double currentPrice = buyEntry.getKey();

            Map<Double, List<Order>> filteredAsks = askOrders.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey() <= currentPrice)
                    //.filter(entry -> entry.getKey() <= buyEntry.getKey())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (filteredAsks.isEmpty()) {
                return MatchOperationResponse.NoPriceMatch;
            }

            List<Order> matchingAskOrders = filteredAsks.values()
                    .stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            List<Order> buyOrders = buyEntry.getValue();

            boolean stillMatching = true;
            while (stillMatching) {
                if (matchingAskOrders.isEmpty() || bidOrders.isEmpty()) {
                    break;
                }
                Order askOrder = getHighestPriorityOrder(matchingAskOrders);
                Order buyOrder = getHighestPriorityOrder(buyOrders);

                int requestedBidQuantity = buyOrder.getQuantity();
                int availableAskQuantity = askOrder.getQuantity();

                if (requestedBidQuantity == availableAskQuantity) {
                    buyOrder.clearQuantity();
                    askOrder.clearQuantity();
                } else if (requestedBidQuantity > availableAskQuantity) {
                    askOrder.clearQuantity();
                    buyOrder.setQuantity(requestedBidQuantity - availableAskQuantity);
                } else {
                    askOrder.setQuantity(availableAskQuantity - requestedBidQuantity);
                    buyOrder.clearQuantity();
                }

                removeProcessedOrder(matchingAskOrders, askOrder);
                removeProcessedOrder(buyOrders, buyOrder);

                if (buyOrders.isEmpty()) {
                    stillMatching = false;
                }
            }
        }

        return MatchOperationResponse.MatchingCompleted;
    }

    private static void removeProcessedOrder(List<Order> ordersToRemoveFrom, Order orderToRemove) {
        if (orderToRemove.isCompleted()) {
            ordersToRemoveFrom.remove(0);
        }
    }

    private static Order getHighestPriorityOrder(List<Order> orders) {
        return orders.get(0);
    }
}
