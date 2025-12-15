package com.example.jbchretreatstore.bookstore.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class CheckoutStatusTest {

    @Test
    fun `all expected checkout statuses exist`() {
        val expectedStatuses = setOf("CHECKED_OUT", "SAVE_FOR_LATER", "PENDING")
        val actualStatuses = CheckoutStatus.entries.map { it.name }.toSet()

        assertEquals(expectedStatuses, actualStatuses)
    }

    @Test
    fun `checkout status count is correct`() {
        assertEquals(3, CheckoutStatus.entries.size)
    }

    @Test
    fun `valueOf returns correct checkout status`() {
        assertEquals(CheckoutStatus.CHECKED_OUT, CheckoutStatus.valueOf("CHECKED_OUT"))
        assertEquals(CheckoutStatus.SAVE_FOR_LATER, CheckoutStatus.valueOf("SAVE_FOR_LATER"))
        assertEquals(CheckoutStatus.PENDING, CheckoutStatus.valueOf("PENDING"))
    }

    @Test
    fun `checkout statuses have unique names`() {
        val names = CheckoutStatus.entries.map { it.name }
        assertEquals(names.size, names.distinct().size)
    }

    @Test
    fun `checked out status exists`() {
        val checkedOut = CheckoutStatus.entries.find { it == CheckoutStatus.CHECKED_OUT }
        assertEquals(CheckoutStatus.CHECKED_OUT, checkedOut)
    }

    @Test
    fun `save for later status exists`() {
        val saveForLater = CheckoutStatus.entries.find { it == CheckoutStatus.SAVE_FOR_LATER }
        assertEquals(CheckoutStatus.SAVE_FOR_LATER, saveForLater)
    }

    @Test
    fun `pending status exists`() {
        val pending = CheckoutStatus.entries.find { it == CheckoutStatus.PENDING }
        assertEquals(CheckoutStatus.PENDING, pending)
    }

    @Test
    fun `ordinal values are sequential`() {
        val ordinals = CheckoutStatus.entries.map { it.ordinal }
        assertEquals(listOf(0, 1, 2), ordinals)
    }
}

