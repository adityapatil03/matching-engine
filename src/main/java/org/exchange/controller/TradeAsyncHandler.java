package org.exchange.controller;

import org.exchange.model.Trade;
import org.exchange.util.OrderBookLogger;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * Receives trades from the Order Book through the blocking queue
 * Waits for the queue to fill up if empty.
 * The actualTradeCount once set by the Main thread, sets the limit
 * for this thread's execution
 */
class TradeAsyncHandler implements Runnable {

    private static final Logger logger = OrderBookLogger.getLogger();

    private final BlockingQueue<Trade> tradeQueue;
    private int emitTradeCount = 0;
    private int actualTradeCount = -1;

    public TradeAsyncHandler(BlockingQueue<Trade> tradeQueue) {
        this.tradeQueue = tradeQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Trade trade = tradeQueue.take();
                emitTrade(trade);

                // some terminating condition
                if (emitTradeCount == actualTradeCount) {
                    return;
                }

            }
        } catch (InterruptedException e) {
            logger.severe(e.getMessage());
        }

    }

    private void emitTrade(Trade trade) {
        // For the sample project, we simply print out the trade

        logger.info(trade.toString());
        emitTradeCount++;
    }

    public void setActualTradeCount(int actualTradeCount) {
        this.actualTradeCount = actualTradeCount;
    }
}
