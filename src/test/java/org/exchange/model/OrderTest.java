package org.exchange.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTest {


    @Test
    @DisplayName("Same price consecutive Orders")
    void consecutive_orders_with_same_price() {
        Order order1 = new LimitOrder("1", Side.Buy, 30, 300);
        Order order2 = new LimitOrder("2", Side.Buy, 30, 300);

        assertTrue(order1.getTime() != order2.getTime(), "Orders must have different creation times for Order Book!");

    }
}
