package za.co.rmb.domain;

import java.util.List;
import java.util.Map;

public interface IMatchOrders {
    MatchOperationResponse matchOrders(Map<Double, List<Order>> askOrders, Map<Double, List<Order>> bidOrders);
}
