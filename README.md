# Matching Engine - Order Book

## Performance benchmark 
 The matching engine currently processes around **600,000 orders per second** in the **async** mode.

## Bids / Asks Data Structure 
 **TreeSet** has been used to store the buy / sell orders.
 TreeSet is based on the **binary search tree** and has **time complexity of O(log n)** to add or remove the elements while keeping the orders sorted as per **price time priority**.
 **BlockingQueue** has been used for producer-consumer pattern for emitting trades.
 
 
## Build - Run

The project can be built with maven and can be run as a simple Java executable jar with an order file as input.

There are 2 options to run the project as follows :-

### java -jar orderbook-1.0-SNAPSHOT.jar <arg1 - inputFile.txt> <optional arg2 - async>
Or
### java -jar orderbook-1.0-SNAPSHOT.jar <arg1 - input file> 
 
#### Examples : 
 java -jar orderbook-1.0-SNAPSHOT.jar *05-orders-SellResting-BuyAggressive.txt* **async**
 
 or
 
 java -jar orderbook-1.0-SNAPSHOT.jar *05-orders-SellResting-BuyAggressive.txt*


**The async (optional) argument in the latter command runs the matching engine in asynchronous mode where Trades are emitted (printed) asynchronously
and so does not interrupt order matching and processing resulting in a better execution time. Here a new Thread would be running as TradeAsyncHandler**.

#### * **_I strongly suggest to additionally try and use async execution mode._** 

## Order Matching Class 
 The class **LimitOrderBook.java** contains main business logic for the order matching and processing.
 

## Tests and Test Files
 **src/test/input** contains test files with various cases which I have composed and tested. 
 JUnit tests and validations are also included. 
