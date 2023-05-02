package org.exchange.orderbook;

import org.exchange.model.Order;
import org.exchange.model.Trade;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;

public interface OrderBook {

    void matchAndProcess(Order inOrder, boolean async);

    void broadcastTrade(Trade trade, boolean async);

    void presentOrderBookStatus();

    void setTradeQueue(BlockingQueue<Trade> tradeQueue);

    void clearOrderBook();

    List<Trade> getTrades();

    TreeSet<Order> getBids();

    TreeSet<Order> getAsks();

}
