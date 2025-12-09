package com.example.jbchretreatstore.bookstore.presentation.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Dimension constants for the JBCH Book Store app.
 * Based on Material Design 3 spacing guidelines and Figma design specifications.
 */
object Dimensions {

    // Spacing Scale
    val spacing_xxs: Dp = 2.dp
    val spacing_xs: Dp = 4.dp
    val spacing_s: Dp = 8.dp
    val spacing_m: Dp = 16.dp
    val spacing_l: Dp = 24.dp
    val spacing_xl: Dp = 32.dp
    val spacing_xxl: Dp = 40.dp
    val spacing_xxxl: Dp = 48.dp
    val spacing_xxxxl: Dp = 64.dp

    // Corner Radius
    val corner_radius_none: Dp = 0.dp
    val corner_radius_xs: Dp = 4.dp
    val corner_radius_s: Dp = 8.dp
    val corner_radius_m: Dp = 12.dp
    val corner_radius_l: Dp = 16.dp
    val corner_radius_xl: Dp = 24.dp
    val corner_radius_xxl: Dp = 32.dp
    val corner_radius_full: Dp = 100.dp

    // Corner Radius Percentages
    const val corner_radius_percent_m: Int = 40 // For pill-shaped elements
    const val corner_radius_percent_l: Int = 50 // For pill-shaped elements
    // Elevation
    val elevation_none: Dp = 0.dp
    val elevation_xs: Dp = 1.dp
    val elevation_s: Dp = 2.dp
    val elevation_m: Dp = 4.dp
    val elevation_l: Dp = 6.dp
    val elevation_xl: Dp = 8.dp
    val elevation_xxl: Dp = 12.dp

    // Component Sizes
    val icon_size_s: Dp = 16.dp
    val icon_size_m: Dp = 24.dp
    val icon_size_l: Dp = 32.dp
    val icon_size_xl: Dp = 48.dp

    val button_height_s: Dp = 32.dp
    val button_height_m: Dp = 40.dp
    val button_height_l: Dp = 48.dp
    val button_height_xl: Dp = 56.dp

    val button_min_width: Dp = 64.dp
    val button_horizontal_padding: Dp = 24.dp
    val button_vertical_padding: Dp = 8.dp

    // Gradient Overlay
    val gradient_overlay_height: Dp = 200.dp

    // Date slider indicator height
    val date_slider_height: Dp = 300.dp

    // Layout Constraints
    val max_content_width: Dp = 500.dp
    val min_touch_target_size: Dp = 48.dp

    // Divider
    val divider_thickness: Dp = 1.dp
    val divider_thickness_thick: Dp = 2.dp

    // Border
    val border_width_thin: Dp = 0.5.dp
    val border_width: Dp = 1.5.dp
    val border_width_thick: Dp = 2.dp

    // Cards
    val card_elevation: Dp = 6.dp
    val card_corner_radius: Dp = 16.dp
    val card_padding: Dp = 16.dp

    // Search Bar
    val search_bar_height: Dp = 48.dp
    val search_bar_corner_radius: Dp = 24.dp
    val search_bar_icon_size: Dp = 24.dp

    // Item List
    val item_card_elevation: Dp = 6.dp
    val item_card_corner_radius: Dp = 14.5.dp
    val item_card_padding: Dp = 16.dp
    val item_spacing: Dp = 12.dp

    // Checkout
    val checkout_container_corner_radius: Dp = 32.dp
    val checkout_button_height: Dp = 48.dp

    // Bottom Navigation
    val bottom_nav_height: Dp = 80.dp
    val bottom_nav_icon_size: Dp = 24.dp
    val bottom_nav_item_spacing: Dp = 8.dp

    // Reorderable Variants List
    val reorderable_item_height: Dp = 64.dp
    val reorderable_list_max_height: Dp = 256.dp
    val reorderable_list_content_padding: Dp = 8.dp
    val reorderable_list_item_spacing: Dp = 8.dp
    val reorderable_item_drag_elevation: Dp = 4.dp

    // Badge
    val badge_size: Dp = 20.dp
    val badge_offset_x: Dp = 12.dp
    val badge_offset_y: Dp = (-4).dp

    // Dropdown Menu
    val dropdown_menu_max_height: Dp = 200.dp
    val dropdown_menu_width: Dp = 180.dp

    // Price Tag
    val price_tag_corner_radius: Dp = 8.dp
    val price_tag_padding_horizontal: Dp = 12.dp
    val price_tag_padding_vertical: Dp = 4.dp

    // Counter (Quantity Selector)
    val counter_button_size: Dp = 32.dp
    val counter_text_width: Dp = 40.dp
    val counter_spacing: Dp = 8.dp
}
