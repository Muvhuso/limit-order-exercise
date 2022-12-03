package za.co.rmb.domain;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {

    private OrderBook classUnderTest;
    MockMatchingEngine mockMatchingEngine;

    @BeforeEach
    public void beforeEachTest() {
        mockMatchingEngine = new MockMatchingEngine();
        classUnderTest = new OrderBook(mockMatchingEngine);
    }

    @Test
    public void addingAnOrder_shouldSet_anOrderId() {
        var order = new Order(1, 1, Direction.Buy);
        var orderResponse = classUnderTest.addOrder(order);

        var response = classUnderTest.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder().getId());
    }

    @Test
    public void addingAnOrder_shouldSet_orderCreatedTime() {
        var order = new Order(1, 1, Direction.Buy);
        var orderResponse = classUnderTest.addOrder(order);

        var response = classUnderTest.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder().getDateTime());
    }

    @Test
    public void addSellOrder_shouldSucceed() {
        var order = new Order(1, 1, Direction.Sell);
        var orderResponse = classUnderTest.addOrder(order);

        var response = classUnderTest.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder().getDateTime());
    }

    @Test
    public void addTwoSellOrders() {
        var orderOne = new Order(1, 1, Direction.Sell);
        var orderResponseOne = classUnderTest.addOrder(orderOne);
        var orderTwo = new Order(1, 1, Direction.Sell);
        var orderResponseTwo = classUnderTest.addOrder(orderTwo);

        var responseOne = classUnderTest.findByOrderId(orderResponseOne.getId());
        var responseTwo = classUnderTest.findByOrderId(orderResponseTwo.getId());

        assertNotNull(responseOne.getOrder().getDateTime());
        assertNotNull(responseTwo.getOrder().getDateTime());
    }

    @Test
    public void shouldAdd_sellAndBuyOrders() {
        var orderOne = new Order(1, 1, Direction.Buy);
        var orderResponseOne = classUnderTest.addOrder(orderOne);

        var orderTwo = new Order(2, 1, Direction.Sell);
        var orderResponseTwo = classUnderTest.addOrder(orderTwo);

        var orderFoundOne = classUnderTest.findByOrderId(orderResponseOne.getId());
        var orderFoundTwo = classUnderTest.findByOrderId(orderResponseTwo.getId());

        assertEquals(orderResponseOne.getId(), orderFoundOne.getOrder().getId());
        assertEquals(orderResponseTwo.getId(), orderFoundTwo.getOrder().getId());
    }

    @Test
    public void findOrder_shouldRespondRespond_withoutOrder_whenItWasNotCreated() {
        var order = new Order(1, 1, Direction.Buy);
        var orderResponse = classUnderTest.addOrder(order);

        var response = classUnderTest.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder().getDateTime());
    }

    @Test
    public void findOrder_shouldRespondRespond_withOrder_whenItWasCreated() {
        var order = new Order(1, 1, Direction.Buy);
        var orderResponse = classUnderTest.addOrder(order);

        var response = classUnderTest.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder());
        assertTrue(response.isOrderFound());
    }

    @Test
    public void modifyOrder_ShouldUpdate_buyOrderQuantity() {
        var orderToAdd = new Order(1, 1, Direction.Buy);
        var orderAddedResponse = classUnderTest.addOrder(orderToAdd);

        classUnderTest.modifyOrder(orderAddedResponse.getId(), 2);

        var orderResponse = classUnderTest.findByOrderId(orderAddedResponse.getId());
        assertEquals(2, orderResponse.getOrder().getQuantity());
    }

    @Test
    public void modifyOrder_ShouldUpdate_buyOrderTime() {
        var orderToAdd = new Order(1, 1, Direction.Buy);
        var orderAddedResponse = classUnderTest.addOrder(orderToAdd);

        classUnderTest.modifyOrder(orderAddedResponse.getId(), 2);

        var orderResponse = classUnderTest.findByOrderId(orderAddedResponse.getId());
        assertEquals(orderAddedResponse.getDateTime(), orderResponse.getOrder().getDateTime());
    }

    @Test
    public void modifyOrder_ShouldUpdate_sellOrderTime() {
        var orderToAdd = new Order(1, 1, Direction.Sell);
        var orderAddedResponse = classUnderTest.addOrder(orderToAdd);

        classUnderTest.modifyOrder(orderAddedResponse.getId(), 2);

        var orderResponse = classUnderTest.findByOrderId(orderAddedResponse.getId());
        assertEquals(orderAddedResponse.getDateTime(), orderResponse.getOrder().getDateTime());
    }

    @Test
    public void modifyOrder_ShouldUpdate_sellOrderQuantity() {
        var orderToAdd = new Order(1, 1, Direction.Sell);
        var orderAddedResponse = classUnderTest.addOrder(orderToAdd);

        classUnderTest.modifyOrder(orderAddedResponse.getId(), 2);

        var orderResponse = classUnderTest.findByOrderId(orderAddedResponse.getId());
        assertEquals(2, orderResponse.getOrder().getQuantity());
    }

    @Test
    public void shouldDeleteAdded_buyOrder() {
        var orderToAdd = new Order(1, 1, Direction.Buy);
        var orderAddedResponse = classUnderTest.addOrder(orderToAdd);

        classUnderTest.deleteOrder(orderAddedResponse.getId());

        var orderResponse = classUnderTest.findByOrderId(orderAddedResponse.getId());
        assertFalse(orderResponse.isOrderFound());
    }

    @Test
    public void shouldDeleteAdded_sellOrder() {
        var orderToAdd = new Order(1, 1, Direction.Sell);
        var orderAddedResponse = classUnderTest.addOrder(orderToAdd);

        classUnderTest.deleteOrder(orderAddedResponse.getId());

        FindOrderResponse orderResponse = classUnderTest.findByOrderId(orderAddedResponse.getId());
        assertFalse(orderResponse.isOrderFound());
    }

    @Test
    public void shouldFindOrdersFor_sellDirection() {
        classUnderTest.addOrder(new Order(10, 1, Direction.Sell));
        classUnderTest.addOrder(new Order(10, 10, Direction.Sell));
        classUnderTest.addOrder(new Order(10, 12, Direction.Sell));

        var orders = classUnderTest.findOrderByPriceAndDirection(Direction.Sell, 10.0);

        assertEquals(3, orders.size());
    }

    @Test
    public void shouldFindOrdersFor_buyDirection() {
        classUnderTest.addOrder(new Order(10, 1, Direction.Buy));
        classUnderTest.addOrder(new Order(10, 10, Direction.Buy));
        classUnderTest.addOrder(new Order(10, 12, Direction.Buy));

        var orders = classUnderTest.findOrderByPriceAndDirection(Direction.Buy, 10.0);

        assertEquals(3, orders.size());
    }

    @Test
    public void shouldEnsure_ordersModified_shouldLoosePriority() {
        var orderToModify = classUnderTest.addOrder(new Order(10, 1, Direction.Sell));
        var unmodifiedOrder = classUnderTest.addOrder(new Order(10, 2, Direction.Sell));

        classUnderTest.modifyOrder(orderToModify.getId(), 10);

        var orders = classUnderTest.findOrderByPriceAndDirection(Direction.Sell, 10.0);

        assertEquals(orders.get(0).getId(), unmodifiedOrder.getId());
        assertEquals(orders.get(1).getId(), orderToModify.getId());
    }

    @Test
    public void matchOrder_shouldRespond_withMatchedResponse() {
        var response = classUnderTest.match();

        assertEquals(MatchOperationResponse.NoPriceMatch, response);
    }
}