package org.exchange.controller;

import org.exchange.model.Order;
import org.exchange.model.Trade;
import org.exchange.orderbook.LimitOrderBook;
import org.exchange.orderbook.OrderBook;
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

    private static final Logger logger = Logger.getLogger(OrderBookHandler.class.getName());

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

            if (Files.size(orderInput) == 0) {
                logger.severe("Input file is empty! ");
                return;
            }

            // If async is true then a Trades will be emitted asynchronously!
            TradeAsyncHandler tradeAsyncHandler = null;
            if (async) {
                BlockingQueue<Trade> tradeQueue = new LinkedBlockingQueue<>();
                orderBook.setTradeQueue(tradeQueue);
                tradeAsyncHandler = new TradeAsyncHandler(tradeQueue);
                tradeAsyncHandler.start();
            }

            String orderEntry = reader.readLine();
            OrderValidator orderValidator = OrderValidator.getInstance();
            int recordsCount = 0;
            Instant executionStart = Instant.now();

            while (orderEntry != null) {
                recordsCount++;
                try {
                    Order inOrder = orderValidator.validateAndParseOrderEntry(orderEntry);

                    // Matching
                    orderBook.matchAndProcess(inOrder, async);

                } catch (MatchingEngineException matchingEngineException) {
                    logger.severe(matchingEngineException.getMessage());
                }

                orderEntry = reader.readLine();
            }

            Instant executionEnd = Instant.now();
            long executionTime = Duration.between(executionStart, executionEnd).toMillis();

            if (!async) {
                orderBook.presentOrderBookStatus();

                // Only for the purpose of Performance statistics
                System.out.println("\n\n (Async = false, Trades Emitted Synchronously) " +
                        "\n\n Time :: * " + executionTime + " * milliseconds for " + orderBook.getTrades().size() + " trades out of " + recordsCount + " orders.");
            } else {
                tradeAsyncHandler.setActualTradeCount(orderBook.getTrades().size());
                tradeAsyncHandler.setRecordsCount(recordsCount);
                tradeAsyncHandler.setExecutionTime(executionTime);
            }

        } catch (IOException e) {
            logger.severe("Input must be a valid file! " + e.getMessage());
        }
    }

    public void clearOrderBook() {
        // Orders can not be modified by further input, fresh order book for next execution
        orderBook.clearOrderBook();
    }

}
