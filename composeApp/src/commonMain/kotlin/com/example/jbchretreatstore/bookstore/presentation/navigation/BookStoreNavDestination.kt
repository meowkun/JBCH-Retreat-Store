package com.example.jbchretreatstore.bookstore.presentation.navigation

sealed class BookStoreNavDestination(val route: String) {
    data object ShopScreen : BookStoreNavDestination(Routes.SHOP)
    data object CheckoutScreen : BookStoreNavDestination(Routes.CHECKOUT)
    data object ReceiptScreen : BookStoreNavDestination(Routes.RECEIPT)

    private object Routes {
        const val SHOP = "ItemListScreen"
        const val CHECKOUT = "CheckoutScreen"
        const val RECEIPT = "ReceiptScreen"
    }
}