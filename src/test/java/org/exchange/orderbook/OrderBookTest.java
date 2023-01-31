package org.exchange.orderbook;

import org.exchange.model.LimitOrder;
import org.exchange.model.Order;
import org.exchange.model.Side;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class OrderBookTest {

    OrderBook orderBook;
    @BeforeEach
    void setUp() {
        orderBook = LimitOrderBook.getInstance();
    }

    @AfterEach
    void clearOrderBook()
    {
        orderBook.clearOrderBook();
    }

    @Test
    @DisplayName("Same price consecutive Orders")
    void consecutive_orders_with_same_price() {
        Order order1 = new LimitOrder("1", Side.Buy, 30, 300);
        orderBook.matchAndProcess(order1, false);
        Order order2 = new LimitOrder("2", Side.Buy, 30, 300);
        orderBook.matchAndProcess(order2, false);

        assertEquals(2, orderBook.getBids().size());
        assertEquals(order1, orderBook.getBids().first());
        assertEquals(order2, orderBook.getBids().last());

    }


    @Test
    @DisplayName("Buy Sell Trade")
    void buy_sell_trade() {
        Order order1 = new LimitOrder("1", Side.Buy, 33, 300);
        orderBook.matchAndProcess(order1, false);
        Order order2 = new LimitOrder("2", Side.Sell, 30, 300);
        orderBook.matchAndProcess(order2, false);

        assertEquals(0, orderBook.getBids().size());
        assertEquals(0, orderBook.getAsks().size());
        assertEquals(1, orderBook.getTrades().size());

    }
}
