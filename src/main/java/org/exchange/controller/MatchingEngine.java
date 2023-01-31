package org.exchange.controller;

import java.nio.file.Path;
import java.util.logging.Logger;

public class MatchingEngine {

    private static final Logger logger = Logger.getLogger(MatchingEngine.class.getName());

    public static void main(String[] args) {

        OrderBookHandler orderBookHandler = OrderBookHandler.getInstance();
        boolean async = false;

        if (args.length == 0) {
            logger.info("Please provide order input file!");
            return;
        }
        if (args.length == 2) {
            String flag = args[1];
            if ("async".equalsIgnoreCase(flag))
                async = true;
        }
        Path orderInput = Path.of(args[0]);
        orderBookHandler.handleOrders(orderInput, async);

        // Orders can not be modified by further input, fresh order book for next execution
        if (!async)
            orderBookHandler.clearOrderBook();
    }
}