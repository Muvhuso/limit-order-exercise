package za.co.rmb.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchingEngineTest {

    MatchingEngine classUnderTest;

    @BeforeEach
    public void beforeEachTest() {
        classUnderTest = new MatchingEngine();
    }

    @Test
    public void matchOrder_shouldRespondWith_NoBidOrdersAvailable_whenBidOrdersAreNotSet() {
        Order order = createAskOrder(10, 1);

        var askOrders = createOrders(order);
        var bidOrders = createOrders();

        var matchOperationResponse =
                classUnderTest.matchOrders(askOrders, bidOrders);

        assertEquals(MatchOperationResponse.NoBidOrdersAvailable, matchOperationResponse);
    }

    @Test
    public void matchOrder_shouldRespondWith_NoAskOrdersAvailable_whenAskOrdersAreNotSet() {
        Order order = createBidOrder(10, 1);
        var bidOrders = createOrders(order);
        var askOrders = createOrders();

        var matchOperationResponse = classUnderTest.matchOrders(askOrders, bidOrders);

        assertEquals(MatchOperationResponse.NoAskOrdersAvailable, matchOperationResponse);
    }

    @Test
    public void matchOrder_shouldRespondWith_noPriceMatch_whenBidOrders_haveNoMatchingAskOrders() {
        var bidOrders = createOrders(createBidOrder(10, 1));
        var askOrders = createOrders(createAskOrder(11, 1));

        var matchOperationResponse = classUnderTest.matchOrders(askOrders, bidOrders);

        assertEquals(MatchOperationResponse.NoPriceMatch, matchOperationResponse);
    }

    @ParameterizedTest
    @ValueSource(doubles = {10.0, 9.8, 0.1})
    public void matchOrder_shouldRespondWith_matchingCompleted_whenBidsHaveNoMatchingAsks(Double price) {
        var bidOrders = createOrders(createBidOrder(10, 1));
        var askOrders = createOrders(createAskOrder(price, 1));

        var matchOperationResponse = classUnderTest.matchOrders(askOrders, bidOrders);

        assertEquals(MatchOperationResponse.MatchingCompleted, matchOperationResponse);
    }

    @Test
    public void givenSuccessfulMatchCompletedOrderMatch_askOrder_shouldBe_updatedToReflect_utilisedQuantity() {
        Order askOrder = createAskOrder(10, 1);
        var bidOrders = createOrders(createBidOrder(10, 1));
        var askOrders = createOrders(askOrder);

        classUnderTest.matchOrders(askOrders, bidOrders);

        assertEquals(0, askOrder.getQuantity());
    }

    @Test
    public void givenSuccessfulMatchCompletedOrderMatch_bidOrder_shouldBe_updatedToReflect_utilisedQuantity() {
        Order bidOrder = createBidOrder(10, 1);
        var bidOrders = createOrders(bidOrder);
        var askOrders = createOrders(createAskOrder(10, 1));

        classUnderTest.matchOrders(askOrders, bidOrders);

        assertEquals(0, bidOrder.getQuantity());
    }

    @Test
    public void matchOrder_canSatisfyBidOrder_fromMoreThanOne_askOrder() {
        Order bidOrder = createBidOrder(10, 1);
        var bidOrders = createOrders(bidOrder);
        Order askOrder = createAskOrder(10, 1);
        var askOrders = createOrders(askOrder, askOrder, askOrder);

        var response = classUnderTest.matchOrders(askOrders, bidOrders);

        assertEquals(MatchOperationResponse.MatchingCompleted, response);
    }

    @Test
    public void matchOrder_canSatisfySellOrder_fromMoreThanOne_bidOrder() {
        var bidOrders = createOrders(
                createBidOrder(10, 1),
                createBidOrder(10, 1),
                createBidOrder(10, 1));
        var askOrders = createOrders(createAskOrder(10, 1));

        var response = classUnderTest.matchOrders(askOrders, bidOrders);

        assertEquals(MatchOperationResponse.MatchingCompleted, response);
    }

    @Test
    public void matchOrder_shouldRespondWith_buyOrderCompletedBy_availableMatchingAskOrders() {
        Order bidOrder = createBidOrder(10, 3);
        var bidOrders = createOrders(bidOrder);
        var askOrders = createOrders(
                createAskOrder(10, 1),
                createAskOrder(10, 1));

        var response = classUnderTest.matchOrders(askOrders, bidOrders);

        assertEquals(MatchOperationResponse.MatchingCompleted, response);
        assertEquals(1, bidOrder.getQuantity());
    }

    @Test
    public void matchOrder_shouldSatisfyAskOrder_withAvailableMatchingBidOrders() {
        Order bidOrderOne = createBidOrder(10, 1);
        Order bidOrderTwo = createBidOrder(10, 1);
        var bidOrders = createOrders(bidOrderOne, bidOrderTwo);

        Order askOrder = createAskOrder(10, 3);
        var askOrders = createOrders(askOrder);

        classUnderTest.matchOrders(askOrders, bidOrders);

        assertEquals(1, askOrder.getQuantity());
    }

    @Test
    public void matchOrder_shouldSatisfyAskOrder_askOrder_whereBidPrice_isHigherThanAskOrder() {
        Order bidOrderOne = createBidOrder(11, 1);
        Order bidOrderTwo = createBidOrder(11, 1);
        var bidOrders = createOrders(bidOrderOne, bidOrderTwo);

        Order askOrder = createAskOrder(10, 3);
        var askOrders = createOrders(askOrder);

        classUnderTest.matchOrders(askOrders, bidOrders);

        assertEquals(1, askOrder.getQuantity());
    }

    private Map<Double, List<Order>> createOrders(Order... orderListToCreate) {
        Map<Double, List<Order>> orderMap = new HashMap<>();

        if (orderListToCreate != null) {
            for (Order order: orderListToCreate) {
                double currentOrderPrice = order.getPrice();
                if (orderMap.containsKey(currentOrderPrice)) {
                    List<Order> existingOrders = orderMap.get(currentOrderPrice);
                    existingOrders.add(order);
                } else {
                    var orders = new LinkedList<Order>();
                    orders.add(order);
                    orderMap.put(currentOrderPrice, orders);
                }

            }
        }

        return orderMap;
    }

    private static Order createAskOrder(double price, int quantity) {
        return new Order(price, quantity, Direction.Sell);
    }

    private static Order createBidOrder(int price, int quantity) {
        return new Order(price, quantity, Direction.Buy);
    }
}