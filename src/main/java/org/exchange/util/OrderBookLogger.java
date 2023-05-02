package org.exchange.util;

import org.exchange.controller.MatchingEngine;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class OrderBookLogger {

    private static final Logger logger = Logger.getLogger(MatchingEngine.class.getName());
    private static FileHandler fileHandler = null;

    public static Logger getLogger() {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%5$s %n");
        try {
            if (fileHandler == null) {
                fileHandler = new FileHandler(Paths.get("").toAbsolutePath() + "/orderbook.log");
                SimpleFormatter formatter = new SimpleFormatter();
                logger.setUseParentHandlers(false);
                logger.addHandler(fileHandler);
                fileHandler.setFormatter(formatter);
            }

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

        return logger;
    }
}
