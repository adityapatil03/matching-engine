package org.exchange.validator;

import org.exchange.model.LimitOrder;
import org.exchange.model.Order;
import org.exchange.model.Side;

public class OrderValidator {

    private static final OrderValidator INSTANCE = new OrderValidator();

    public static OrderValidator getInstance() {
        return INSTANCE;
    }

    /** validateAndParseOrderEntry() validates format of the input and
     * throws a custom MatchingEngineException.
     * Alternatively, an error file can be generated for all invalid records
     **/
    public Order validateAndParseOrderEntry(String orderEntry) throws MatchingEngineException {
        if (orderEntry == null || orderEntry.isEmpty())
            throw new MatchingEngineException("Empty order entry!");

        String[] orderParams = orderEntry.split(",");
        if (orderParams.length != 4) {
            // Log and skip order
            throw new MatchingEngineException("Invalid number of attributes for order entry : " + orderEntry);
        }

        if (orderParams[0].isEmpty() || orderParams[1].isEmpty() || orderParams[2].isEmpty() || orderParams[3].isEmpty())
            throw new MatchingEngineException("One of the order attribute is empty for order entry : " + orderEntry);

        if (!Side.Buy.label.equals(orderParams[1]) && !Side.Sell.label.equals(orderParams[1]))
            throw new MatchingEngineException("Invalid Buy / Sell Side for order entry : " + orderEntry);

        try {
            String orderId = orderParams[0];
            Side side = Side.Buy.label.equals(orderParams[1]) ? Side.Buy : Side.Sell;
            int price = Integer.parseInt(orderParams[2]);
            int volume = Integer.parseInt(orderParams[3]);

            if (price <= 0 || volume <= 0)
                throw new MatchingEngineException("Values for price / volume must be greater than 0. Invalid order entry : " + orderEntry);

            return new LimitOrder(orderId, side, price, volume);
        } catch (NumberFormatException e) {
            throw new MatchingEngineException("Invalid price / volume for order entry : " + orderEntry);
        }
    }

}
