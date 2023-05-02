package org.exchange.controller;

import org.exchange.model.Order;
import org.exchange.model.Trade;
import org.exchange.orderbook.LimitOrderBook;
import org.exchange.orderbook.OrderBook;
import org.exchange.util.OrderBookLogger;
import org.exchange.validator.MatchingEngineException;
import org.exchange.validator.OrderValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class OrderBookHandler {

    private static final Logger logger = OrderBookLogger.getLogger();
    private static final OrderBookHandler INSTANCE = new OrderBookHandler();

    public static OrderBookHandler getInstance() {
        return INSTANCE;
    }

    private final OrderBook orderBook = LimitOrderBook.getInstance();


    public void handleOrders(Path orderInput, boolean... asyncArg) {
        boolean async = false;
        if (asyncArg.length > 0) {
            async = asyncArg[0];
        }

        try (BufferedReader reader = Files.newBufferedReader(orderInput)) {

            if (isFileEmpty(orderInput)) {
                return;
            }
            // If async is true then Trades will be emitted asynchronously!
            TradeAsyncHandler tradeAsyncRunnable = null;
            Thread thread = null;
            if (async) {
                BlockingQueue<Trade> tradeQueue = new LinkedBlockingQueue<>();
                orderBook.setTradeQueue(tradeQueue);
                tradeAsyncRunnable = new TradeAsyncHandler(tradeQueue);
                thread = new Thread(tradeAsyncRunnable, "Trade Emitter");
                thread.start();
            }

            String orderEntry = reader.readLine();
            OrderValidator orderValidator = OrderValidator.getInstance();
            int recordsCount = 0;
            Instant executionStart = Instant.now();
            if (!async) {
                System.out.println("\n  Processing orders and emitting trades simultaneously... ");
            }

            while (orderEntry != null) {
                recordsCount++;
                try {
                    Order inOrder = orderValidator.validateAndParseOrderEntry(orderEntry);

                    //*** Call to Matching logic
                    orderBook.matchAndProcess(inOrder, async);

                } catch (MatchingEngineException matchingEngineException) {
                    System.err.println(matchingEngineException.getMessage());
                }
                orderEntry = reader.readLine();
            }

            Instant executionEnd = Instant.now();
            long executionTime = Duration.between(executionStart, executionEnd).toMillis();

            // Only for the purpose of Performance statistics
            printStatMessage(executionTime, recordsCount, async);

            if (async) {

                // set count for terminating condition for the trade handler thread
                tradeAsyncRunnable.setActualTradeCount(orderBook.getTrades().size());
                System.out.println("\n  All Orders processed, emitting trades ... ");

                // wait for the trade handler thread to finish emitting trades
                thread.join();
            }
            orderBook.presentOrderBookStatus();

            System.out.println("\n *Please check the output in orderbook.log inside current directory to see the Trades executed and final Order Book status.");

        } catch (IOException e) {
            logger.severe("Input must be a valid file! " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearOrderBook() {
        // Orders can not be modified by further input, fresh order book for next execution
        orderBook.clearOrderBook();
    }

    private void printStatMessage(long executionTime, int recordsCount, boolean async) {
        String statMessage;
        if (!async) {
            statMessage = "\n\n (Async = false, Trades Emitted Synchronously) " +
                    "\n\n Time :: * " + executionTime + " * milliseconds for " + orderBook.getTrades().size() + " trades out of " + recordsCount + " orders.";
        } else {
            statMessage = "\n\n (Async = true, Trades Emitted Asynchronously) " +
                    "\n\n *** Time :: * " + executionTime + " * milliseconds for " + orderBook.getTrades().size() + " trades out of " + recordsCount + " orders. ***\n\n " +
                    "* (Please note this execution time is the time OrderBook took" +
                    " to match and process all the orders while emitting trades via asynchronous blocking queue.)";
        }

        System.out.println("\n  * Completed *" + statMessage);

    }

    private boolean isFileEmpty(Path orderInput) throws IOException {

        if (Files.size(orderInput) == 0) {
            System.err.println("Input file is empty! ");
            return true;
        }
        return false;
    }

}
