package com.example.jbchretreatstore.bookstore.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaymentMethodTest {

    @Test
    fun `all payment methods have correct method names`() {
        assertEquals("Cash", PaymentMethod.CASH.methodName)
        assertEquals("Credit Card", PaymentMethod.CREDIT_CARD.methodName)
        assertEquals("Debit Card", PaymentMethod.DEBIT_CARD.methodName)
        assertEquals("E-Wallet", PaymentMethod.E_WALLET.methodName)
        assertEquals("Zelle", PaymentMethod.ZELLE.methodName)
        assertEquals("Venmo", PaymentMethod.VENMO.methodName)
    }

    @Test
    fun `all expected payment methods exist`() {
        val expectedMethods = setOf(
            "CASH", "CREDIT_CARD", "DEBIT_CARD", "E_WALLET", "ZELLE", "VENMO"
        )
        val actualMethods = PaymentMethod.entries.map { it.name }.toSet()

        assertEquals(expectedMethods, actualMethods)
    }

    @Test
    fun `payment method count is correct`() {
        assertEquals(6, PaymentMethod.entries.size)
    }

    @Test
    fun `valueOf returns correct payment method`() {
        assertEquals(PaymentMethod.CASH, PaymentMethod.valueOf("CASH"))
        assertEquals(PaymentMethod.CREDIT_CARD, PaymentMethod.valueOf("CREDIT_CARD"))
        assertEquals(PaymentMethod.DEBIT_CARD, PaymentMethod.valueOf("DEBIT_CARD"))
        assertEquals(PaymentMethod.E_WALLET, PaymentMethod.valueOf("E_WALLET"))
        assertEquals(PaymentMethod.ZELLE, PaymentMethod.valueOf("ZELLE"))
        assertEquals(PaymentMethod.VENMO, PaymentMethod.valueOf("VENMO"))
    }

    @Test
    fun `payment methods have unique names`() {
        val names = PaymentMethod.entries.map { it.name }
        assertEquals(names.size, names.distinct().size)
    }

    @Test
    fun `payment methods have unique method names`() {
        val methodNames = PaymentMethod.entries.map { it.methodName }
        assertEquals(methodNames.size, methodNames.distinct().size)
    }

    @Test
    fun `method names are not empty`() {
        PaymentMethod.entries.forEach { method ->
            assertTrue(method.methodName.isNotEmpty())
        }
    }

    @Test
    fun `method names are not blank`() {
        PaymentMethod.entries.forEach { method ->
            assertTrue(method.methodName.isNotBlank())
        }
    }
}

