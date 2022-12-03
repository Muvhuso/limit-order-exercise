package za.co.rmb.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookIntegrationTest {

    @Test
    public void matchOrder_shouldMatchBids_toAskOrders() {
        OrderBook classUnderTest = new OrderBook(new MatchingEngine());

        Order orderOne = classUnderTest.addOrder(new Order(9, 40, Direction.Buy));
        Order orderTwo = classUnderTest.addOrder(new Order(9, 20, Direction.Buy));
        classUnderTest.addOrder(new Order(8, 30, Direction.Buy));
        classUnderTest.addOrder(new Order(8, 20, Direction.Buy));
        classUnderTest.addOrder(new Order(7, 50, Direction.Buy));
        classUnderTest.addOrder(new Order(7, 50, Direction.Buy));

        classUnderTest.addOrder(new Order(10, 5, Direction.Sell));
        classUnderTest.addOrder(new Order(10, 100, Direction.Sell));
        classUnderTest.addOrder(new Order(11, 40, Direction.Sell));
        classUnderTest.addOrder(new Order(11, 50, Direction.Sell));
        classUnderTest.addOrder(new Order(12, 20, Direction.Sell));
        classUnderTest.addOrder(new Order(12, 10, Direction.Sell));

        classUnderTest.addOrder(new Order(9, 55, Direction.Sell));

        classUnderTest.match();

        assertEquals(0, orderOne.getQuantity());
        assertEquals(5, orderTwo.getQuantity());
    }
}