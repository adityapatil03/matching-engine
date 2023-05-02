package org.exchange.orderbook;


import org.exchange.model.Order;
import org.exchange.model.Side;
import org.exchange.model.Trade;
import org.exchange.util.OrderBookLogger;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class LimitOrderBook implements OrderBook {

    private static final Logger logger = OrderBookLogger.getLogger();
    private static final OrderBook INSTANCE = new LimitOrderBook();

    public static OrderBook getInstance() {
        return INSTANCE;
    }

    private final List<Trade> trades = new ArrayList<>();
    private BlockingQueue<Trade> tradeQueue;

    /**
     * TreeSet has time complexity of O(log(n))
     * to add or remove elements while keeping
     * the list sorted
     * <p>
     * -- java.util.concurrent.ConcurrentSkipListSet -- can be used in place of TreeSet
     * if multithreading is implemented here
     */
    private final TreeSet<Order> bids = new TreeSet<>(new OrderComparator());
    private final TreeSet<Order> asks = new TreeSet<>(new OrderComparator());

    /**
     * matchAndProcess(Order order)
     * matches bids and asks as per price time priority
     * against incoming aggressive order
     * Params: Order -- Incoming Limit Order,
     * async -- for Asynchronous handling of Trades
     */
    @Override
    public void matchAndProcess(Order inOrder, boolean async) {

        boolean isBuy = Side.Buy.equals(inOrder.getSide());

        while ((isBuy && asks.size() > 0 && inOrder.getPrice() >= asks.first().getPrice()) || (!isBuy && bids.size() > 0 && inOrder.getPrice() <= bids.first().getPrice())) {
            int tradeVolume;
            Order restingOrder = isBuy ? asks.first() : bids.first();

            // minimum of 2 volumes as trade volume
            tradeVolume = Math.min(restingOrder.getVolume(), inOrder.getVolume());

            // One of the two order volume would be 0 ?
            restingOrder.setVolume(restingOrder.getVolume() - tradeVolume);
            inOrder.setVolume(inOrder.getVolume() - tradeVolume);
            Trade trade = new Trade(inOrder.getOrderId(), restingOrder.getOrderId(), restingOrder.getPrice(), tradeVolume);

            // Handle trade
            broadcastTrade(trade, async);

            if (restingOrder.getVolume() == 0) {
                if (isBuy)
                    asks.remove(restingOrder);
                else
                    bids.remove(restingOrder);
            }

            if (inOrder.getVolume() == 0)
                break;

        }

        if (inOrder.getVolume() > 0) {
            // keep as resting order
            if (isBuy)
                bids.add(inOrder);
            else {
                asks.add(inOrder);
            }

        }

    }

    @Override
    public void broadcastTrade(Trade trade, boolean async) {

        trades.add(trade);

        if (!async) {
            // Emit to System.out Synchronously, affects total execution time
            logger.info(trade.toString());
        } else {
            // Emit to TradeAsyncHandler Asynchronously, improves total execution time
            tradeQueue.add(trade);
        }

    }

    @Override
    public void presentOrderBookStatus() {
        Iterator<Order> buyIt = this.bids.iterator();
        Iterator<Order> sellIt = this.asks.iterator();
        DecimalFormat volFormatter = new DecimalFormat("###,###,###", new DecimalFormatSymbols(Locale.US));
        DecimalFormat priceFormatter = new DecimalFormat("######", new DecimalFormatSymbols(Locale.US));

        if (this.bids.size() == 0 && this.asks.size() == 0) {
            System.out.println("All orders have been matched!");
            return;
        }

        logger.info("\n" + String.format("%-21s", "Buys") + "Sells\n");

        while (buyIt.hasNext() || sellIt.hasNext()) {
            String buy = "";
            String sell = "";

            if (buyIt.hasNext()) {
                Order buyOrder = buyIt.next();
                buy = String.format("%1$11s", volFormatter.format(buyOrder.getVolume())) + " " + String.format("%1$6s", priceFormatter.format(buyOrder.getPrice()));
            }

            if (sellIt.hasNext()) {
                Order sellOrder = sellIt.next();
                sell = String.format("%1$6s", priceFormatter.format(sellOrder.getPrice())) + " " + String.format("%1$11s", volFormatter.format(sellOrder.getVolume()));
            }

            logger.info((buy.isBlank() ? String.format("%18s", "") : buy) + " | " + sell);
        }

    }

    @Override
    public List<Trade> getTrades() {
        return trades;
    }

    @Override
    public TreeSet<Order> getBids() {
        return bids;
    }

    @Override
    public TreeSet<Order> getAsks() {
        return asks;
    }

    @Override
    public void setTradeQueue(BlockingQueue<Trade> tradeQueue) {
        this.tradeQueue = tradeQueue;
    }

    @Override
    public void clearOrderBook() {
        this.bids.clear();
        this.asks.clear();
        this.trades.clear();
    }
}

/**
 * Compares 2 orders as per price time priority :
 * First with price (most aggressive to least aggressive),
 * then by arrival time into the book (oldest to newest)
 */
class OrderComparator implements Comparator<Order> {
    @Override
    public int compare(Order order1, Order order2) {
        if (order1.getPrice() == order2.getPrice())
            return Long.compare(order1.getTime(), order2.getTime());
        else {
            if (order1.getSide().equals(Side.Buy))
                return Integer.compare(order2.getPrice(), order1.getPrice());
            return Integer.compare(order1.getPrice(), order2.getPrice());
        }
    }
}


