package com.example.jbchretreatstore.bookstore.presentation.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BookStoreNavDestinationTest {

    @Test
    fun `ShopScreen has correct route`() {
        assertEquals("ItemListScreen", BookStoreNavDestination.ShopScreen.route)
    }

    @Test
    fun `CheckoutScreen has correct route`() {
        assertEquals("CheckoutScreen", BookStoreNavDestination.CheckoutScreen.route)
    }

    @Test
    fun `ReceiptScreen has correct route`() {
        assertEquals("ReceiptScreen", BookStoreNavDestination.ReceiptScreen.route)
    }

    @Test
    fun `all destinations have unique routes`() {
        val routes = listOf(
            BookStoreNavDestination.ShopScreen.route,
            BookStoreNavDestination.CheckoutScreen.route,
            BookStoreNavDestination.ReceiptScreen.route
        )

        assertEquals(routes.size, routes.distinct().size)
    }

    @Test
    fun `ShopScreen is not equal to CheckoutScreen`() {
        assertNotEquals<BookStoreNavDestination>(
            BookStoreNavDestination.ShopScreen,
            BookStoreNavDestination.CheckoutScreen
        )
    }

    @Test
    fun `ShopScreen is not equal to ReceiptScreen`() {
        assertNotEquals<BookStoreNavDestination>(
            BookStoreNavDestination.ShopScreen,
            BookStoreNavDestination.ReceiptScreen
        )
    }

    @Test
    fun `CheckoutScreen is not equal to ReceiptScreen`() {
        assertNotEquals<BookStoreNavDestination>(
            BookStoreNavDestination.CheckoutScreen,
            BookStoreNavDestination.ReceiptScreen
        )
    }

    @Test
    fun `same destination equals itself`() {
        assertEquals<BookStoreNavDestination>(
            BookStoreNavDestination.ShopScreen,
            BookStoreNavDestination.ShopScreen
        )
        assertEquals<BookStoreNavDestination>(
            BookStoreNavDestination.CheckoutScreen,
            BookStoreNavDestination.CheckoutScreen
        )
        assertEquals<BookStoreNavDestination>(
            BookStoreNavDestination.ReceiptScreen,
            BookStoreNavDestination.ReceiptScreen
        )
    }
}

