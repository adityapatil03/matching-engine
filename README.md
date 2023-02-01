# Matching Engine - Order Book

The project can be built with maven and can be run as a simple Java executable jar with an order file as input.

There are 2 options to run the project as follows :-

### java -jar orderbook-1.0-SNAPSHOT.jar <arg1 - input file> 
Or
### java -jar orderbook-1.0-SNAPSHOT.jar <arg1 - inputFile.txt> <optional arg2 - async>
 
** Examples : 
 java -jar orderbook-1.0-SNAPSHOT.jar *05-orders-SellResting-BuyAggressive.txt*
 
 or
 
 java -jar orderbook-1.0-SNAPSHOT.jar *05-orders-SellResting-BuyAggressive.txt* **async**
(*Input File as first argument and async as optional 2nd argument)

The **async (optional)** argument in the latter command runs the matching engine in asynchronous mode where Trades are emitted (printed) asynchronously
and so does not interrupt order matching and processing resulting in a better execution time. Here a new Thread would be running as TradeAsyncHandler.

*I strongly suggest to also use and try async execution option in addition. 

## Order Matching Class : The class LimitOrderBook.java contains main business logic for the order matching and processing.

## Performance benchmark 
 The matching engine currently processes around **300,000 orders per second**.

## Bids / Asks Data Structure 
 **TreeSet** has been used to store the buy / sell orders.
 TreeSet is based on the **binary search tree** and has **time complexity of O(log n)** to add or remove the elements while keeping the orders sorted as per **price time priority**.

## Tests 
 JUnit tests and validations are part of the deliverable. **src/test/input** contains various test files I have composed and tested. 
