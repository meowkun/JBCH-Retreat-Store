package com.example.jbchretreatstore.bookstore.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PaymentMethodTest {

    @Test
    fun `all expected payment methods exist`() {
        val expectedMethods =
            setOf("CASH", "CREDIT_CARD", "DEBIT_CARD", "E_WALLET", "ZELLE", "VENMO")
        val actualMethods = PaymentMethod.entries.map { it.name }.toSet()

        assertEquals(expectedMethods, actualMethods)
    }

    @Test
    fun `payment method count is correct`() {
        assertEquals(6, PaymentMethod.entries.size)
    }

    @Test
    fun `valueOf returns correct payment method for CASH`() {
        assertEquals(PaymentMethod.CASH, PaymentMethod.valueOf("CASH"))
    }

    @Test
    fun `valueOf returns correct payment method for CREDIT_CARD`() {
        assertEquals(PaymentMethod.CREDIT_CARD, PaymentMethod.valueOf("CREDIT_CARD"))
    }

    @Test
    fun `valueOf returns correct payment method for DEBIT_CARD`() {
        assertEquals(PaymentMethod.DEBIT_CARD, PaymentMethod.valueOf("DEBIT_CARD"))
    }

    @Test
    fun `valueOf returns correct payment method for E_WALLET`() {
        assertEquals(PaymentMethod.E_WALLET, PaymentMethod.valueOf("E_WALLET"))
    }

    @Test
    fun `valueOf returns correct payment method for ZELLE`() {
        assertEquals(PaymentMethod.ZELLE, PaymentMethod.valueOf("ZELLE"))
    }

    @Test
    fun `valueOf returns correct payment method for VENMO`() {
        assertEquals(PaymentMethod.VENMO, PaymentMethod.valueOf("VENMO"))
    }

    @Test
    fun `payment methods have unique names`() {
        val names = PaymentMethod.entries.map { it.name }
        assertEquals(names.size, names.distinct().size)
    }

    @Test
    fun `payment methods have unique ordinals`() {
        val ordinals = PaymentMethod.entries.map { it.ordinal }
        assertEquals(ordinals.size, ordinals.distinct().size)
    }

    @Test
    fun `ordinal values are sequential`() {
        val ordinals = PaymentMethod.entries.map { it.ordinal }
        assertEquals(listOf(0, 1, 2, 3, 4, 5), ordinals)
    }

    @Test
    fun `CASH is the first payment method`() {
        assertEquals(0, PaymentMethod.CASH.ordinal)
    }

    @Test
    fun `VENMO is the last payment method`() {
        assertEquals(5, PaymentMethod.VENMO.ordinal)
    }

    @Test
    fun `entries returns all payment methods`() {
        val entries = PaymentMethod.entries

        assertTrue(entries.contains(PaymentMethod.CASH))
        assertTrue(entries.contains(PaymentMethod.CREDIT_CARD))
        assertTrue(entries.contains(PaymentMethod.DEBIT_CARD))
        assertTrue(entries.contains(PaymentMethod.E_WALLET))
        assertTrue(entries.contains(PaymentMethod.ZELLE))
        assertTrue(entries.contains(PaymentMethod.VENMO))
    }

    @Test
    fun `payment methods are not equal to each other`() {
        val methods = PaymentMethod.entries
        for (i in methods.indices) {
            for (j in methods.indices) {
                if (i != j) {
                    assertNotEquals(methods[i], methods[j])
                }
            }
        }
    }

    @Test
    fun `payment method name property returns correct value`() {
        assertEquals("CASH", PaymentMethod.CASH.name)
        assertEquals("CREDIT_CARD", PaymentMethod.CREDIT_CARD.name)
        assertEquals("DEBIT_CARD", PaymentMethod.DEBIT_CARD.name)
        assertEquals("E_WALLET", PaymentMethod.E_WALLET.name)
        assertEquals("ZELLE", PaymentMethod.ZELLE.name)
        assertEquals("VENMO", PaymentMethod.VENMO.name)
    }

    @Test
    fun `payment method methodName property returns display name`() {
        assertEquals("Cash", PaymentMethod.CASH.methodName)
        assertEquals("Credit Card", PaymentMethod.CREDIT_CARD.methodName)
        assertEquals("Debit Card", PaymentMethod.DEBIT_CARD.methodName)
        assertEquals("E-Wallet", PaymentMethod.E_WALLET.methodName)
        assertEquals("Zelle", PaymentMethod.ZELLE.methodName)
        assertEquals("Venmo", PaymentMethod.VENMO.methodName)
    }

    @Test
    fun `toString returns the enum name`() {
        PaymentMethod.entries.forEach { method ->
            assertEquals(method.name, method.toString())
        }
    }

    @Test
    fun `payment methods can be used in when expression`() {
        PaymentMethod.entries.forEach { method ->
            val result = when (method) {
                PaymentMethod.CASH -> "cash"
                PaymentMethod.CREDIT_CARD -> "card"
                PaymentMethod.DEBIT_CARD -> "debit"
                PaymentMethod.E_WALLET -> "ewallet"
                PaymentMethod.ZELLE -> "zelle"
                PaymentMethod.VENMO -> "venmo"
            }
            assertTrue(result.isNotEmpty())
        }
    }

    @Test
    fun `payment methods can be compared`() {
        assertTrue(PaymentMethod.CASH < PaymentMethod.CREDIT_CARD)
        assertTrue(PaymentMethod.CREDIT_CARD < PaymentMethod.DEBIT_CARD)
        assertTrue(PaymentMethod.DEBIT_CARD < PaymentMethod.E_WALLET)
        assertTrue(PaymentMethod.E_WALLET < PaymentMethod.ZELLE)
        assertTrue(PaymentMethod.ZELLE < PaymentMethod.VENMO)
    }

    @Test
    fun `payment methods can be iterated`() {
        var count = 0
        for (method in PaymentMethod.entries) {
            count++
        }
        assertEquals(6, count)
    }

    @Test
    fun `payment methods work with collection operations`() {
        val filtered = PaymentMethod.entries.filter {
            it.name.contains("_")
        }
        // CREDIT_CARD, DEBIT_CARD, E_WALLET
        assertEquals(3, filtered.size)
    }

    @Test
    fun `payment methods work with map operations`() {
        val lengths = PaymentMethod.entries.map { it.name.length }
        assertEquals(listOf(4, 11, 10, 8, 5, 5), lengths)
    }

    @Test
    fun `CASH name length is 4`() {
        assertEquals(4, PaymentMethod.CASH.name.length)
    }

    @Test
    fun `CREDIT_CARD name length is 11`() {
        assertEquals(11, PaymentMethod.CREDIT_CARD.name.length)
    }

    @Test
    fun `electronic payment methods include ZELLE and VENMO`() {
        val electronicMethods = listOf(PaymentMethod.ZELLE, PaymentMethod.VENMO)

        electronicMethods.forEach { method ->
            assertTrue(PaymentMethod.entries.contains(method))
        }
    }

    @Test
    fun `non-cash payment methods count is 5`() {
        val nonCash = PaymentMethod.entries.filter { it != PaymentMethod.CASH }
        assertEquals(5, nonCash.size)
    }

    @Test
    fun `card payment methods include CREDIT_CARD and DEBIT_CARD`() {
        val cardMethods = PaymentMethod.entries.filter { it.name.contains("CARD") }
        assertEquals(2, cardMethods.size)
        assertTrue(cardMethods.contains(PaymentMethod.CREDIT_CARD))
        assertTrue(cardMethods.contains(PaymentMethod.DEBIT_CARD))
    }

    @Test
    fun `selectableOptions contains ZELLE, VENMO, and CASH`() {
        val options = PaymentMethod.selectableOptions

        assertEquals(3, options.size)
        assertEquals(PaymentMethod.ZELLE, options[0])
        assertEquals(PaymentMethod.VENMO, options[1])
        assertEquals(PaymentMethod.CASH, options[2])
    }

    @Test
    fun `selectableOptions does not contain CREDIT_CARD, DEBIT_CARD, or E_WALLET`() {
        val options = PaymentMethod.selectableOptions

        assertTrue(PaymentMethod.CREDIT_CARD !in options)
        assertTrue(PaymentMethod.DEBIT_CARD !in options)
        assertTrue(PaymentMethod.E_WALLET !in options)
    }
}
