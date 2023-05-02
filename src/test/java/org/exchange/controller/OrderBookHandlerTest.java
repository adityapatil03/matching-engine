package org.exchange.controller;

import org.exchange.orderbook.LimitOrderBook;
import org.exchange.orderbook.OrderBook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderBookHandlerTest {

    OrderBookHandler orderBookHandler;
    OrderBook orderBook;
    private Path testResource;

    @BeforeEach
    void setUp() {
        orderBookHandler = OrderBookHandler.getInstance();
        orderBook = LimitOrderBook.getInstance();
        testResource = Path.of("src/test/input");
    }

    @AfterEach
    void clearOrderBook()
    {
        orderBookHandler.clearOrderBook();
    }

    @Test
    @DisplayName("Sample orders resting")
    void sample_orders_resting() {
        Path file = testResource.resolve("01-orders-assignmentSample.txt");
        orderBookHandler.handleOrders(file);
        assertEquals(0, orderBook.getTrades().size());
    }

    @Test
    @DisplayName("Sample orders traded")
    void sample_orders_traded() {
        Path file = testResource.resolve("02-orders-assignmentSample.txt");
        orderBookHandler.handleOrders(file);
        assertTrue(orderBook.getTrades().size() > 0);
    }

    @Test
    @DisplayName("Same price resting orders")
    void resting_orders_with_same_price() {
        Path file = testResource.resolve("03-orders-samePriceResting.txt");
        orderBookHandler.handleOrders(file);
        int totalInputOrders = 12; // no of records in the input, should count no of lines in the file
        assertEquals(totalInputOrders, orderBook.getBids().size());
    }

    @Test
    @DisplayName("Large aggressive sell")
    void large_aggressive_sell_on_resting_buys() {
        Path file = testResource.resolve("04-orders-largeAggressiveSell.txt");
        orderBookHandler.handleOrders(file);
        assertEquals(0, orderBook.getBids().size());
    }

    @Test
    @DisplayName("Resting Sell and aggressive buy")
    void resting_sell_aggressive_buy() {
        Path file = testResource.resolve("05-orders-SellResting-BuyAggressive.txt");
        orderBookHandler.handleOrders(file);
        assertEquals(0, orderBook.getAsks().size());
    }

    @Test
    @DisplayName("Single large resting buy order with aggressive matches")
    void large_resting_buy_order_with_aggressive_matches() {
        Path file = testResource.resolve("06-orders-largeResting-fullAggressiveMatch.txt");
        orderBookHandler.handleOrders(file);
        assertTrue(orderBook.getBids().size()>0);
    }

    @Test
    @DisplayName("Only sell orders pending")
    void only_sell_orders_pending() {
        Path file = testResource.resolve("11-orders-noBuyPending.txt");
        orderBookHandler.handleOrders(file);
        assertEquals(0, orderBook.getBids().size());
        assertTrue(orderBook.getAsks().size()>0);
        assertTrue(orderBook.getTrades().size()>0);

    }

    @Test
    @DisplayName("All matched")
    void all_orders_matched() {
        Path file = testResource.resolve("12-orders-allMatched.txt");
        orderBookHandler.handleOrders(file);
        assertEquals(0, orderBook.getBids().size());
        assertEquals(0, orderBook.getAsks().size());
        assertTrue(orderBook.getTrades().size()>0);
    }

    @Test
    @DisplayName("Performance load - 5K orders")
    void performance_load_5K_orders() {
        Path file = testResource.resolve("09-orders-performance5000.txt");
        orderBookHandler.handleOrders(file);
    }

    @Test
    @DisplayName("Performance load - 100K orders")
    void performance_load_100K_orders() {
        Path file = testResource.resolve("10-orders-performanceLoad-100K.txt");
        orderBookHandler.handleOrders(file);
    }

    @Test
    @DisplayName("Performance load - 600K orders")
    void performance_load_600K_orders() {
        Path file = testResource.resolve("15-orders-performanceLoad-600K.txt");
        orderBookHandler.handleOrders(file);
    }

    @Test
    @DisplayName("Performance load - 1 million orders")
    void performance_load_1M_orders() {
        Path file = testResource.resolve("00-orders-performanceLoad-1M.txt");
        orderBookHandler.handleOrders(file);
    }

    // Test invalid file data for validator... validator test .java  IllegalArgumentException?
}
