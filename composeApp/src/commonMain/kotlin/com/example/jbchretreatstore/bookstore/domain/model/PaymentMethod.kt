package com.example.jbchretreatstore.bookstore.domain.model

enum class PaymentMethod(val methodName: String) {
    CASH("Cash"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    E_WALLET("E-Wallet"),
    ZELLE("Zelle"),
    VENMO("Venmo"),
}
