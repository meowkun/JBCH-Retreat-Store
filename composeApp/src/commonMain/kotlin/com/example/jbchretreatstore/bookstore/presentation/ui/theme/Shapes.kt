package com.example.jbchretreatstore.bookstore.presentation.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape

/**
 * Shape definitions for the JBCH Book Store app.
 * Based on Material Design 3 shape system.
 */
object Shapes {

    // None
    val none: Shape = RoundedCornerShape(Dimensions.corner_radius_none)

    // Extra Small
    val extraSmall: Shape = RoundedCornerShape(Dimensions.corner_radius_xs)

    // Small
    val small: Shape = RoundedCornerShape(Dimensions.corner_radius_s)

    // Medium
    val medium: Shape = RoundedCornerShape(Dimensions.corner_radius_m)

    // Large
    val large: Shape = RoundedCornerShape(Dimensions.corner_radius_l)

    // Extra Large
    val extraLarge: Shape = RoundedCornerShape(Dimensions.corner_radius_xl)

    // Extra Extra Large
    val extraExtraLarge: Shape = RoundedCornerShape(Dimensions.corner_radius_xxl)

    // Full/Circle
    val full: Shape = CircleShape

    // Component Specific Shapes
    val button: Shape = RoundedCornerShape(Dimensions.corner_radius_m)
    val buttonRound: Shape = RoundedCornerShape(Dimensions.corner_radius_full)

    val card: Shape = RoundedCornerShape(Dimensions.card_corner_radius)
    val itemCard: Shape = RoundedCornerShape(Dimensions.item_card_corner_radius)

    val searchBar: Shape = RoundedCornerShape(Dimensions.search_bar_corner_radius)

    val priceTag: Shape = RoundedCornerShape(Dimensions.price_tag_corner_radius)

    val badge: Shape = CircleShape

    val bottomSheet: Shape = RoundedCornerShape(
        topStart = Dimensions.checkout_container_corner_radius,
        topEnd = Dimensions.checkout_container_corner_radius
    )

    val dialog: Shape = RoundedCornerShape(Dimensions.corner_radius_xl)

    val dropdown: Shape = RoundedCornerShape(Dimensions.corner_radius_s)

    // Top Rounded Only (for container views)
    val topRounded: Shape = RoundedCornerShape(
        topStart = Dimensions.checkout_container_corner_radius,
        topEnd = Dimensions.checkout_container_corner_radius
    )

    // Bottom Rounded Only
    val bottomRounded: Shape = RoundedCornerShape(
        bottomStart = Dimensions.corner_radius_l,
        bottomEnd = Dimensions.corner_radius_l
    )
}

