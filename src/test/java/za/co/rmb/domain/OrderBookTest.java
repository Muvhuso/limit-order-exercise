package za.co.rmb.domain;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {

    private OrderBook classUnderTest;

    @BeforeEach
    public void beforeEachTest() {
        classUnderTest = new OrderBook();
    }

    @Test
    public void addingAnOrder_shouldSet_anOrderId() {
        Order order = new Order(1, 1, Direction.Buy);
        Order orderResponse = classUnderTest.addOrder(order);

        FindOrderResponse response = classUnderTest.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder().getId());
    }

    @Test
    public void addingAnOrder_shouldSet_orderCreatedTime() {
        Order order = new Order(1, 1, Direction.Buy);
        Order orderResponse = classUnderTest.addOrder(order);

        FindOrderResponse response = classUnderTest.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder().getDateTime());
    }

    @Test
    public void addSellOrder_shouldSucceed() {
        Order order = new Order(1, 1, Direction.Sell);
        Order orderResponse = classUnderTest.addOrder(order);

        FindOrderResponse response = classUnderTest.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder().getDateTime());
    }

    @Test
    public void addTwoSellOrders() {
        Order orderOne = new Order(1, 1, Direction.Sell);
        Order orderResponseOne = classUnderTest.addOrder(orderOne);
        Order orderTwo = new Order(1, 1, Direction.Sell);
        Order orderResponseTwo = classUnderTest.addOrder(orderTwo);

        FindOrderResponse responseOne = classUnderTest.findByOrderId(orderResponseOne.getId());
        FindOrderResponse responseTwo = classUnderTest.findByOrderId(orderResponseTwo.getId());

        assertNotNull(responseOne.getOrder().getDateTime());
        assertNotNull(responseTwo.getOrder().getDateTime());
    }

    @Test
    public void shouldAdd_sellAndBuyOrders() {
        Order orderOne = new Order(1, 1, Direction.Buy);
        Order orderResponseOne = classUnderTest.addOrder(orderOne);

        Order orderTwo = new Order(2, 1, Direction.Sell);
        Order orderResponseTwo = classUnderTest.addOrder(orderTwo);

        FindOrderResponse orderFoundOne = classUnderTest.findByOrderId(orderResponseOne.getId());
        FindOrderResponse orderFoundTwo = classUnderTest.findByOrderId(orderResponseTwo.getId());

        assertEquals(orderResponseOne.getId(), orderFoundOne.getOrder().getId());
        assertEquals(orderResponseTwo.getId(), orderFoundTwo.getOrder().getId());
    }

    @Test
    public void findOrder_shouldRespondRespond_withoutOrder_whenItWasNotCreated() {
        Order order = new Order(1, 1, Direction.Buy);
        Order orderResponse = classUnderTest.addOrder(order);

        FindOrderResponse response = classUnderTest.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder().getDateTime());
    }

    @Test
    public void findOrder_shouldRespondRespond_withOrder_whenItWasCreated() {
        Order order = new Order(1, 1, Direction.Buy);
        Order orderResponse = classUnderTest.addOrder(order);

        FindOrderResponse response = classUnderTest.findByOrderId(orderResponse.getId());

        assertNotNull(response.getOrder());
        assertTrue(response.isOrderFound());
    }

    @Test
    public void modifyOrder_ShouldUpdate_buyOrderQuantity() {
        Order orderToAdd = new Order(1, 1, Direction.Buy);
        Order orderAddedResponse = classUnderTest.addOrder(orderToAdd);

        classUnderTest.modifyOrder(orderAddedResponse.getId(), 2);

        FindOrderResponse orderResponse = classUnderTest.findByOrderId(orderAddedResponse.getId());
        assertEquals(2, orderResponse.getOrder().getQuantity());
    }

    @Test
    public void modifyOrder_ShouldUpdate_buyOrderTime() {
        Order orderToAdd = new Order(1, 1, Direction.Buy);
        Order orderAddedResponse = classUnderTest.addOrder(orderToAdd);

        classUnderTest.modifyOrder(orderAddedResponse.getId(), 2);

        FindOrderResponse orderResponse = classUnderTest.findByOrderId(orderAddedResponse.getId());
        assertEquals(orderAddedResponse.getDateTime(), orderResponse.getOrder().getDateTime());
    }

    @Test
    public void modifyOrder_ShouldUpdate_sellOrderTime() {
        Order orderToAdd = new Order(1, 1, Direction.Sell);
        Order orderAddedResponse = classUnderTest.addOrder(orderToAdd);

        classUnderTest.modifyOrder(orderAddedResponse.getId(), 2);

        FindOrderResponse orderResponse = classUnderTest.findByOrderId(orderAddedResponse.getId());
        assertEquals(orderAddedResponse.getDateTime(), orderResponse.getOrder().getDateTime());
    }

    @Test
    public void modifyOrder_ShouldUpdate_sellOrderQuantity() {
        Order orderToAdd = new Order(1, 1, Direction.Sell);
        Order orderAddedResponse = classUnderTest.addOrder(orderToAdd);

        classUnderTest.modifyOrder(orderAddedResponse.getId(), 2);

        FindOrderResponse orderResponse = classUnderTest.findByOrderId(orderAddedResponse.getId());
        assertEquals(2, orderResponse.getOrder().getQuantity());
    }

    @Test
    public void shouldDeleteAdded_buyOrder() {
        Order orderToAdd = new Order(1, 1, Direction.Buy);
        Order orderAddedResponse = classUnderTest.addOrder(orderToAdd);

        classUnderTest.deleteOrder(orderAddedResponse.getId());

        FindOrderResponse orderResponse = classUnderTest.findByOrderId(orderAddedResponse.getId());
        assertFalse(orderResponse.isOrderFound());
    }

    @Test
    public void shouldDeleteAdded_sellOrder() {
        Order orderToAdd = new Order(1, 1, Direction.Sell);
        Order orderAddedResponse = classUnderTest.addOrder(orderToAdd);

        classUnderTest.deleteOrder(orderAddedResponse.getId());

        FindOrderResponse orderResponse = classUnderTest.findByOrderId(orderAddedResponse.getId());
        assertFalse(orderResponse.isOrderFound());
    }

    @Test
    public void shouldFindOrdersFor_sellDirection() {
        classUnderTest.addOrder(new Order(10, 1, Direction.Sell));
        classUnderTest.addOrder(new Order(10, 10, Direction.Sell));
        classUnderTest.addOrder(new Order(10, 12, Direction.Sell));

        List<Order> orders = classUnderTest.findOrderByPriceAndDirection(Direction.Sell, 10.0);

        assertEquals(3, orders.size());
    }

    @Test
    public void shouldFindOrdersFor_buyDirection() {
        classUnderTest.addOrder(new Order(10, 1, Direction.Buy));
        classUnderTest.addOrder(new Order(10, 10, Direction.Buy));
        classUnderTest.addOrder(new Order(10, 12, Direction.Buy));

        List<Order> orders = classUnderTest.findOrderByPriceAndDirection(Direction.Buy, 10.0);

        assertEquals(3, orders.size());
    }

    @Test
    public void shouldEnsure_ordersModified_shouldLoosePriority() {
        Order orderToModify = classUnderTest.addOrder(new Order(10, 1, Direction.Sell));
        Order unmodifiedOrder = classUnderTest.addOrder(new Order(10, 2, Direction.Sell));

        classUnderTest.modifyOrder(orderToModify.getId(), 10);

        List<Order> orders = classUnderTest.findOrderByPriceAndDirection(Direction.Sell, 10.0);

        assertEquals(orders.get(0).getId(), unmodifiedOrder.getId());
        assertEquals(orders.get(1).getId(), orderToModify.getId());
    }

    @Test
    public void matchOrder_shouldRespondWith_NoBids_whenBids_areNotSet() {
        classUnderTest.addOrder(new Order(10, 1, Direction.Sell));

        MatchOperationResponse matchOperationResponse = classUnderTest.matchOrder();

        assertEquals(MatchOperationResponse.NoBids, matchOperationResponse);
    }

    @Test
    public void matchOrder_shouldRespondWith_NoAsks_whenAsks_areNotSet() {
        classUnderTest.addOrder(new Order(10, 1, Direction.Buy));

        MatchOperationResponse matchOperationResponse = classUnderTest.matchOrder();

        assertEquals(MatchOperationResponse.NoAsks, matchOperationResponse);
    }

    @Test
    public void matchOrder_shouldRespondWith_noPriceMatch_when_BidsHaveNoMatchingAsks() {
        classUnderTest.addOrder(new Order(10, 1, Direction.Buy));
        classUnderTest.addOrder(new Order(11, 1, Direction.Sell));

        MatchOperationResponse matchOperationResponse = classUnderTest.matchOrder();

        assertEquals(MatchOperationResponse.NoPriceMatch, matchOperationResponse);
    }

    @ParameterizedTest
    @ValueSource(doubles = {10.0, 9.8, 0.1})
    public void matchOrder_shouldRespondWith_matchingCompleted_when_bidsHaveNoMatchingAsks(Double price) {
        classUnderTest.addOrder(new Order(10, 1, Direction.Buy));
        classUnderTest.addOrder(new Order(price, 1, Direction.Sell));

        MatchOperationResponse matchOperationResponse = classUnderTest.matchOrder();

        assertEquals(MatchOperationResponse.MatchingCompleted, matchOperationResponse);
    }

    @Test
    public void givenA_successfulMatchCompleted_askOrder_shouldBe_updatedToReflect_utilisedQuantity() {
        classUnderTest.addOrder(new Order(10, 1, Direction.Buy));
        Order order = classUnderTest.addOrder(new Order(10, 1, Direction.Sell));
        classUnderTest.matchOrder();

        FindOrderResponse foundOrder = classUnderTest.findByOrderId(order.getId());

        assertEquals(0, foundOrder.getOrder().getQuantity());
    }

    @Test
    public void matchOrder_canSatisfyBidOrder_fromMoreThanOne_askOrder() {
        classUnderTest.addOrder(new Order(10, 3, Direction.Buy));
        classUnderTest.addOrder(new Order(10, 1, Direction.Sell));
        classUnderTest.addOrder(new Order(10, 1, Direction.Sell));
        classUnderTest.addOrder(new Order(10, 1, Direction.Sell));

        MatchOperationResponse matchOperationResponse = classUnderTest.matchOrder();

        assertEquals(MatchOperationResponse.MatchingCompleted, matchOperationResponse);
    }

    @Test
    public void matchOrder_canSatisfySellOrder_fromMoreThanOne_bidOrder() {
        classUnderTest.addOrder(new Order(10, 3, Direction.Sell));
        classUnderTest.addOrder(new Order(10, 1, Direction.Buy));
        classUnderTest.addOrder(new Order(10, 1, Direction.Buy));
        classUnderTest.addOrder(new Order(10, 1, Direction.Buy));

        MatchOperationResponse matchOperationResponse = classUnderTest.matchOrder();

        assertEquals(MatchOperationResponse.MatchingCompleted, matchOperationResponse);
    }

    @Test
    public void matchOrder_shouldRespondWith_buyOrderCompletedBy_availableMatchingAskOrders() {
        Order buyOrder = classUnderTest.addOrder(new Order(10, 3, Direction.Buy));
        classUnderTest.addOrder(new Order(10, 1, Direction.Sell));
        classUnderTest.addOrder(new Order(10, 1, Direction.Sell));
        classUnderTest.matchOrder();

        FindOrderResponse foundOrder = classUnderTest.findByOrderId(buyOrder.getId());

        assertEquals(1, foundOrder.getOrder().getQuantity());
    }

    @Test
    public void matchOrder_canShouldRespondWith_askOrderCompletedBy_availableMatchingBuyOrders() {
        Order buyOrder = classUnderTest.addOrder(new Order(10, 3, Direction.Sell));
        classUnderTest.addOrder(new Order(10, 1, Direction.Buy));
        classUnderTest.addOrder(new Order(10, 1, Direction.Buy));
        classUnderTest.matchOrder();

        FindOrderResponse foundOrder = classUnderTest.findByOrderId(buyOrder.getId());

        assertEquals(1, foundOrder.getOrder().getQuantity());
    }
}