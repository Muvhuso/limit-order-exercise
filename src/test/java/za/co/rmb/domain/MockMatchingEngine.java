package za.co.rmb.domain;

import java.util.List;
import java.util.Map;

public class MockMatchingEngine implements IMatchOrders {
    @Override
    public MatchOperationResponse matchOrders(Map<Double, List<Order>> askOrders, Map<Double, List<Order>> bidOrders) {
        return MatchOperationResponse.NoPriceMatch;
    }
}
