package com.wallstreet.stock.market.simulation.model.enums;

/**
 * Enumeration of possible notification types within the application.
 */
public enum NotificationType {
    
    // Trade-related notifications
    TRADE_EXECUTED_BUY,        // "Your buy order for 10 shares of AAPL at $150.00 was executed."
    TRADE_EXECUTED_SELL,       // "Your sell order for 10 shares of AAPL at $150.00 was executed."
    PARTIAL_TRADE_EXECUTED,    // "Your buy order for 100 shares of AAPL was partially filled (10 shares at $150.00)."

    // Order status notifications
    ORDER_EXPIRED,             // "Your market order for AAPL has expired."
    ORDER_CANCELLED,           // "Your limit order for AAPL has been cancelled."
    
    // GTT-related notifications
    GTT_TRIGGERED,             // "Your GTT order for AAPL has been triggered and is now active."

    // General notifications
    WELCOME,                   // "Welcome to the Stock Market Simulation!"
    ADMIN_MESSAGE              // General message from the administrator.
}
