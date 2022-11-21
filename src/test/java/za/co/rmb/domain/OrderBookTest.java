package za.co.rmb.domain;


import org.junit.jupiter.api.Test;

import java.util.LinkedList;

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
    public void addTwoSellOrders() {
        OrderBook orderBook = new OrderBook();
        Order orderOne = new Order(1, 1, Direction.Sell);
        Order orderResponseOne = orderBook.addOrder(orderOne);
        Order orderTwo = new Order(1, 1, Direction.Sell);
        Order orderResponseTwo = orderBook.addOrder(orderTwo);

        FindOrderResponse responseOne = orderBook.findByOrderId(orderResponseOne.getId());
        FindOrderResponse responseTwo = orderBook.findByOrderId(orderResponseTwo.getId());

        assertNotNull(responseOne.getOrder().getDateTime());
        assertNotNull(responseTwo.getOrder().getDateTime());
    }

    @Test
    public void shouldAdd_sellAndBuyOrders() {
        OrderBook orderBook = new OrderBook();

        Order orderOne = new Order(1, 1, Direction.Buy);
        Order orderResponseOne = orderBook.addOrder(orderOne);

        Order orderTwo = new Order(2, 1, Direction.Sell);
        Order orderResponseTwo = orderBook.addOrder(orderTwo);

        FindOrderResponse orderFoundOne = orderBook.findByOrderId(orderResponseOne.getId());
        FindOrderResponse orderFoundTwo = orderBook.findByOrderId(orderResponseTwo.getId());

        assertEquals(orderResponseOne.getId(), orderFoundOne.getOrder().getId());
        assertEquals(orderResponseTwo.getId(), orderFoundTwo.getOrder().getId());
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
    public void modifyOrder_ShouldUpdate_buyOrderQuantity() {
        OrderBook orderBook = new OrderBook();

        Order orderToAdd = new Order(1, 1, Direction.Buy);
        Order orderAddedResponse = orderBook.addOrder(orderToAdd);

        orderBook.modifyOrder(orderAddedResponse.getId(), 2);

        FindOrderResponse orderResponse = orderBook.findByOrderId(orderAddedResponse.getId());
        assertEquals(2, orderResponse.getOrder().getQuantity());
    }

    @Test
    public void modifyOrder_ShouldUpdate_buyOrderTime() {
        OrderBook orderBook = new OrderBook();

        Order orderToAdd = new Order(1, 1, Direction.Buy);
        Order orderAddedResponse = orderBook.addOrder(orderToAdd);

        orderBook.modifyOrder(orderAddedResponse.getId(), 2);

        FindOrderResponse orderResponse = orderBook.findByOrderId(orderAddedResponse.getId());
        assertEquals(orderAddedResponse.getDateTime(), orderResponse.getOrder().getDateTime());
    }

    @Test
    public void modifyOrder_ShouldUpdate_sellOrderTime() {
        OrderBook orderBook = new OrderBook();

        Order orderToAdd = new Order(1, 1, Direction.Sell);
        Order orderAddedResponse = orderBook.addOrder(orderToAdd);

        orderBook.modifyOrder(orderAddedResponse.getId(), 2);

        FindOrderResponse orderResponse = orderBook.findByOrderId(orderAddedResponse.getId());
        assertEquals(orderAddedResponse.getDateTime(), orderResponse.getOrder().getDateTime());
    }

    @Test
    public void modifyOrder_ShouldUpdate_sellOrderQuantity() {
        OrderBook orderBook = new OrderBook();

        Order orderToAdd = new Order(1, 1, Direction.Sell);
        Order orderAddedResponse = orderBook.addOrder(orderToAdd);

        orderBook.modifyOrder(orderAddedResponse.getId(), 2);

        FindOrderResponse orderResponse = orderBook.findByOrderId(orderAddedResponse.getId());
        assertEquals(2, orderResponse.getOrder().getQuantity());
    }

    @Test
    public void shouldDeleteAdded_buyOrder() {
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

    @Test
    public void shouldFindOrdersFor_sellDirection() {
        OrderBook orderBook = new OrderBook();
        orderBook.addOrder(new Order(10, 1, Direction.Sell));
        orderBook.addOrder(new Order(10, 10, Direction.Sell));
        orderBook.addOrder(new Order(10, 12, Direction.Sell));

        LinkedList<Order> orders = orderBook.findOrderByPriceAndDirection(Direction.Sell, 10.0);

        assertEquals(3, orders.size());
    }

    @Test
    public void shouldFindOrdersFor_buyDirection() {
        OrderBook orderBook = new OrderBook();
        orderBook.addOrder(new Order(10, 1, Direction.Buy));
        orderBook.addOrder(new Order(10, 10, Direction.Buy));
        orderBook.addOrder(new Order(10, 12, Direction.Buy));

        LinkedList<Order> orders = orderBook.findOrderByPriceAndDirection(Direction.Buy, 10.0);

        assertEquals(3, orders.size());
    }

    @Test
    public void shouldEnsure_ordersModified_shouldLoosePriority() throws InterruptedException {

        OrderBook orderBook = new OrderBook();
        Order orderToModify = orderBook.addOrder(new Order(10, 1, Direction.Sell));
        Order unmodifiedOrder = orderBook.addOrder(new Order(10, 2, Direction.Sell));

        orderBook.modifyOrder(orderToModify.getId(), 10);

        LinkedList<Order> orders = orderBook.findOrderByPriceAndDirection(Direction.Sell, 10.0);

        assertEquals(orders.get(0).getId(), unmodifiedOrder.getId());
        assertEquals(orders.get(1).getId(), orderToModify.getId());
    }
}