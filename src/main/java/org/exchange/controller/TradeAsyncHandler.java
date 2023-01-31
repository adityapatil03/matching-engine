package org.exchange.controller;

import org.exchange.model.Trade;
import org.exchange.orderbook.LimitOrderBook;
import org.exchange.orderbook.OrderBook;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * Receives trades from the Order Book through the blocking queue
 * Waits for the queue to fill up if empty.
 * The actualTradeCount once set by the Main thread, sets the limit
 * for this thread's execution
 */
class TradeAsyncHandler extends Thread {

    private static final Logger logger = Logger.getLogger(MatchingEngine.class.getName());

    OrderBook orderBook = LimitOrderBook.getInstance();
    private final BlockingQueue<Trade> tradeQueue;
    private int emitTradeCount = 0;
    private int actualTradeCount = -1;
    private int recordsCount;
    private long executionTime;

    public TradeAsyncHandler(BlockingQueue<Trade> tradeQueue) {
        this.tradeQueue = tradeQueue;
        this.setName("Trade Emitter");
    }

    public void run() {
        try {

            while (true) {
                Trade trade = tradeQueue.take();
                System.out.println(trade);
                emitTradeCount++;
                if (emitTradeCount == actualTradeCount) {
                    orderBook.presentOrderBookStatus();
                    System.out.println("\n\n (Async = true, Trades Emitted Asynchronously) " +
                            "\n\n Time :: * " + executionTime + " * milliseconds for " + actualTradeCount + " trades out of " + recordsCount + " orders.\n\n " +
                            "**(Please note this execution time is the time OrderBook took" +
                            " to match and process all the orders while emitting trades via asynchronous blocking queue." +
                            "\n   Rest of the time (not shown here) required for printing trades to System.out I/O stream, handled separately by TradeAsyncHandler!");
                    orderBook.clearOrderBook();
                    return;
                }

            }
        } catch (InterruptedException e) {
            logger.severe(e.getMessage());
        }

    }

    public void setActualTradeCount(int actualTradeCount) {
        this.actualTradeCount = actualTradeCount;
    }

    public void setRecordsCount(int recordsCount) {
        this.recordsCount = recordsCount;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}
