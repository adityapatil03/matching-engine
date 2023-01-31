package org.exchange.validator;

import org.exchange.controller.OrderBookHandler;
import org.exchange.orderbook.OrderBook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class OrderValidatorTest {

    OrderValidator orderValidator;
    private Path testResource;

    @BeforeEach
    void setUp() {
        orderValidator = OrderValidator.getInstance();
    }


    @Test
    @DisplayName("Invalid buy/sell Side")
    void invalid_buySell_side() {
        String orderEntry = "10001,C,100,500";
        try {
            orderValidator.validateAndParseOrderEntry(orderEntry);
            fail("Should have thrown MatchingEngineException as input has invalid buy/sell Side");
        } catch (MatchingEngineException e) {
            assertEquals(e.getMessage(), "Invalid Buy / Sell Side for order entry : "+ orderEntry);
        }
    }

    @Test
    @DisplayName("Invalid input arguments length")
    void invalid_input_arguments_length() {
        String orderEntry = "10001,XYZ,B,100,500";
        try {
            orderValidator.validateAndParseOrderEntry(orderEntry);
            fail("Should have thrown MatchingEngineException as input has invalid number of arguments");
        } catch (MatchingEngineException e) {
            assertEquals(e.getMessage(), "Invalid number of attributes for order entry : "+ orderEntry);
        }
    }

    @Test
    @DisplayName("Price less than zero")
    void price_less_than_zero() {
        String orderEntry = "10001,S,-100,500";
        try {
            orderValidator.validateAndParseOrderEntry(orderEntry);
            fail("Should have thrown MatchingEngineException as input has price less than zero");
        } catch (MatchingEngineException e) {
            assertEquals(e.getMessage(), "Values for price / volume must be greater than 0. Invalid order entry : "+ orderEntry);
        }
    }

    @Test
    @DisplayName("Input has no buy/sell side")
    void input_has_no_buy_sell_side() {
        String orderEntry = "10001,,100,500";
        try {
            orderValidator.validateAndParseOrderEntry(orderEntry);
            fail("Should have thrown MatchingEngineException as input has no buy/sell side");
        } catch (MatchingEngineException e) {
            assertEquals(e.getMessage(), "One of the order attribute is empty for order entry : "+ orderEntry);
        }
    }

    @Test
    @DisplayName("Volume is not a number")
    void volume_is_not_a_number() {
        String orderEntry = "10001,B,100,5AA";
        try {
            orderValidator.validateAndParseOrderEntry(orderEntry);
            fail("Should have thrown MatchingEngineException as volume is not a number");
        } catch (MatchingEngineException e) {
            assertEquals(e.getMessage(), "Invalid price / volume for order entry : "+ orderEntry);
        }
    }
}
