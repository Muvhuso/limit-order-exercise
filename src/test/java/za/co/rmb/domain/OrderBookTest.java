package za.co.rmb.domain;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {

    @Test
    public void addingAnOrder_shouldSet_anOrderId() {
        OrderBook orderBook = new OrderBook();

        Order order = new Order(1, 1, Direction.Buy);
        Order orderResponse = orderBook.addOrder(order);

        FindOrderResponse response = orderBook.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder().getId());
    }

    @Test
    public void addingAnOrder_shouldSet_orderCreatedTime() {
        OrderBook orderBook = new OrderBook();

        Order order = new Order(1, 1, Direction.Buy);
        Order orderResponse = orderBook.addOrder(order);

        FindOrderResponse response = orderBook.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder().getDateTime());
    }

    @Test
    public void addSellOrder_shouldSucceed() {
        OrderBook orderBook = new OrderBook();

        Order order = new Order(1, 1, Direction.Sell);
        Order orderResponse = orderBook.addOrder(order);

        FindOrderResponse response = orderBook.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder().getDateTime());
    }

    @Test
    public void findOrder_shouldRespondRespond_withoutOrder_whenItWasNotCreated() {
        OrderBook orderBook = new OrderBook();

        Order order = new Order(1, 1, Direction.Buy);
        Order orderResponse = orderBook.addOrder(order);

        FindOrderResponse response = orderBook.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder().getDateTime());
    }

    @Test
    public void findOrder_shouldRespondRespond_withOrder_whenItWasCreated() {
        OrderBook orderBook = new OrderBook();

        Order order = new Order(1, 1, Direction.Buy);
        Order orderResponse = orderBook.addOrder(order);

        FindOrderResponse response = orderBook.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder());
        assertTrue(response.isOrderFound());
    }

    @Test
    public void modifyOrder_ShouldUpdate_theQuantityForTheOrder() {
        OrderBook orderBook = new OrderBook();

        Order orderToAdd = new Order(1, 1, Direction.Buy);
        Order orderAddedResponse = orderBook.addOrder(orderToAdd);

        orderBook.modifyOrder(orderAddedResponse.getId(), 2);

        FindOrderResponse orderResponse = orderBook.findByOrderId(orderAddedResponse.getId());
        assertEquals(2, orderResponse.getOrder().getQuantity());
    }

    @Test
    public void modifyOrder_ShouldUpdate_theTimeForTheOrder() {
        OrderBook orderBook = new OrderBook();

        Order orderToAdd = new Order(1, 1, Direction.Buy);
        Order orderAddedResponse = orderBook.addOrder(orderToAdd);

        orderBook.modifyOrder(orderAddedResponse.getId(), 2);

        FindOrderResponse orderResponse = orderBook.findByOrderId(orderAddedResponse.getId());
        assertEquals(orderAddedResponse.getDateTime(), orderResponse.getOrder().getDateTime());
    }

    @Test
    public void shouldDeleteAdded_order() {
        OrderBook orderBook = new OrderBook();

        Order orderToAdd = new Order(1, 1, Direction.Buy);
        Order orderAddedResponse = orderBook.addOrder(orderToAdd);

        orderBook.deleteOrder(orderAddedResponse.getId());

        FindOrderResponse orderResponse = orderBook.findByOrderId(orderAddedResponse.getId());
        assertFalse(orderResponse.isOrderFound());
    }

    @Test
    public void shouldDeleteAdded_sellOrder() {
        OrderBook orderBook = new OrderBook();

        Order orderToAdd = new Order(1, 1, Direction.Sell);
        Order orderAddedResponse = orderBook.addOrder(orderToAdd);

        orderBook.deleteOrder(orderAddedResponse.getId());

        FindOrderResponse orderResponse = orderBook.findByOrderId(orderAddedResponse.getId());
        assertFalse(orderResponse.isOrderFound());
    }

}