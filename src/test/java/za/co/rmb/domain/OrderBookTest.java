package za.co.rmb.domain;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderBookTest {

    @Test
    public void addingAnOrder_shouldSet_anOrderId() {
        OrderBook orderBook = new OrderBook();

        Order order = new Order(1, 1, Direction.Buy);
        Order orderResponse = orderBook.addOrder(order);

        Order orderFound = orderBook.findByOrderId(orderResponse.getId());

        assertNotNull(orderFound.getId());
    }

    @Test
    public void addingAnOrder_shouldSet_orderCreatedTime() {
        OrderBook orderBook = new OrderBook();

        Order order = new Order(1, 1, Direction.Buy);
        Order orderResponse = orderBook.addOrder(order);

        Order orderFound = orderBook.findByOrderId(orderResponse.getId());

        assertNotNull(orderFound.getDateTime());
    }

}