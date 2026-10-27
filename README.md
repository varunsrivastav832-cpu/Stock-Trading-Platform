# Stock Trading Platform (Java Swing)

A desktop-based stock trading simulation built with Java Swing. It uses sample market prices and virtual money; it does not connect to a live exchange or perform real trades.

## Features
- Sample market watchlist with stock symbols and prices
- Simulated buy and sell operations
- Virtual starting cash of ₹100,000
- Portfolio holdings and current value
- Transaction history
- Available cash, portfolio value, profit/loss, and total shares summary
- Validation for invalid quantities, insufficient cash, and selling more shares than owned
- Uses Java classes, collections, and `ArrayList`

## Requirements
- Java JDK 17 or newer

## Run on macOS, Windows, or Linux
Open a terminal in the folder containing `StockTradingPlatform.java`, then run:

```bash
javac StockTradingPlatform.java
java StockTradingPlatform
```

## Notes
- Prices are sample values for educational use only.
- Trades are simulated and data is kept in memory while the app is running. Closing the app resets the session.
