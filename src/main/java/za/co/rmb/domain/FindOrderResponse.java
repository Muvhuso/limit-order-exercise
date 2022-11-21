package za.co.rmb.domain;

public class FindOrderResponse {
    private boolean orderFound;
    private Order order;

    private FindOrderResponse(boolean orderFound, Order order) {
        this.orderFound = orderFound;
        this.order = order;
    }

    public static FindOrderResponse orderNotFound() {
        return new FindOrderResponse(false, null);
    }

    public static FindOrderResponse orderResult(Order order) {
        return new FindOrderResponse(true, order);
    }

    public boolean isOrderFound() {
        return orderFound;
    }

    public Order getOrder() {
        return order;
    }
}
