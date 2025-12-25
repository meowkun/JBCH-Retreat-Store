package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.utils.filterNumericInputWithMaxDecimals
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.add_item_dialog_title
import jbchretreatstore.composeapp.generated.resources.add_item_save
import jbchretreatstore.composeapp.generated.resources.edit_item_dialog_title
import jbchretreatstore.composeapp.generated.resources.edit_item_update
import org.jetbrains.compose.resources.StringResource

data class AddItemState(
    val newItem: DisplayItem = DisplayItem(),
    val newItemVariant: DisplayItem.Variant = DisplayItem.Variant(),
    val displayAddOptionView: Boolean = false,
    val showItemNameError: Boolean = false,
    val showItemPriceError: Boolean = false,
    val showAddOptionError: Boolean = false,
    val showValueError: Boolean = false,
    val showDuplicateKeyError: Boolean = false,
    val displayPrice: String = "",
    val optionValue: String = "",
    val isEditMode: Boolean = false
) {
    // ==================== Derived Properties ====================

    /** Whether the variant key field is enabled (only when no values have been added yet) */
    val isOptionKeyEnabled: Boolean
        get() = newItemVariant.valueList.isEmpty()

    /** Whether the item has variants to display */
    val hasVariants: Boolean
        get() = newItem.variants.isNotEmpty()

    /** The dialog title resource based on edit mode */
    val dialogTitleRes: StringResource
        get() = if (isEditMode) Res.string.edit_item_dialog_title else Res.string.add_item_dialog_title

    /** The save button text resource based on edit mode */
    val saveButtonTextRes: StringResource
        get() = if (isEditMode) Res.string.edit_item_update else Res.string.add_item_save

    /** Whether to show error on name field */
    val showNameFieldError: Boolean
        get() = showItemNameError && newItem.name.isBlank()

    /** Whether to show error on price field */
    val showPriceFieldError: Boolean
        get() = showItemPriceError && newItem.price <= 0.0

    /** Whether to show error on variant key field */
    val showVariantKeyFieldError: Boolean
        get() = showAddOptionError || showDuplicateKeyError

    // ==================== Field Update Functions ====================

    /** Updates the item name */
    fun updateName(name: String): AddItemState = copy(
        newItem = newItem.copy(name = name),
        showItemNameError = false
    )

    /** Updates the item price from string input */
    fun updatePrice(input: String): AddItemState? {
        val filtered = input.filterNumericInputWithMaxDecimals() ?: return null
        return copy(
            displayPrice = filtered,
            newItem = newItem.copy(price = filtered.toDoubleOrNull() ?: 0.0),
            showItemPriceError = false
        )
    }

    /** Updates the variant key */
    fun updateVariantKey(key: String): AddItemState = copy(
        newItemVariant = newItemVariant.copy(key = key),
        showAddOptionError = false,
        showDuplicateKeyError = false
    )

    /** Updates the option value input */
    fun updateOptionValue(value: String): AddItemState = copy(
        optionValue = value,
        showValueError = false
    )

    // ==================== List Management Functions ====================

    /** Reorders the variants list */
    fun reorderVariants(reorderedVariants: List<DisplayItem.Variant>): AddItemState = copy(
        newItem = newItem.copy(variants = reorderedVariants)
    )

    /** Removes a variant from the list */
    fun removeVariant(variantToRemove: DisplayItem.Variant): AddItemState = copy(
        newItem = newItem.copy(
            variants = newItem.variants.filter { it != variantToRemove }
        )
    )

    /** Reorders the variant values list */
    fun reorderVariantValues(reorderedValues: List<String>): AddItemState = copy(
        newItemVariant = newItemVariant.copy(valueList = reorderedValues)
    )

    /** Removes a value from the variant values list */
    fun removeVariantValue(valueToRemove: String): AddItemState = copy(
        newItemVariant = newItemVariant.copy(
            valueList = newItemVariant.valueList.filter { it != valueToRemove }
        )
    )

    // ==================== Navigation Functions ====================

    /** Shows the add option view */
    fun showAddOptionView(): AddItemState = copy(displayAddOptionView = true)

    /** Returns a new state after navigating back from add option view */
    fun navigateBack(): AddItemState = copy(
        displayAddOptionView = false,
        showAddOptionError = false,
        showValueError = false,
        newItemVariant = DisplayItem.Variant(),
        optionValue = ""
    )

    // ==================== Validation Functions ====================

    /**
     * Validates the item and returns it if valid, or null with error flags set
     */
    fun validateItem(): Pair<DisplayItem?, AddItemState> {
        val isNameValid = newItem.name.isNotBlank()
        val isPriceValid = newItem.price > 0.0

        return if (isNameValid && isPriceValid) {
            newItem to this
        } else {
            null to copy(
                showItemNameError = !isNameValid,
                showItemPriceError = !isPriceValid
            )
        }
    }

    /**
     * Saves the current variant (if valid) and navigates back to the main view
     */
    fun saveVariantAndNavigateBack(): AddItemState {
        val isOptionValid = newItemVariant.key.isNotBlank() &&
                newItemVariant.valueList.isNotEmpty()

        return if (isOptionValid) {
            copy(
                displayAddOptionView = false,
                showAddOptionError = false,
                showValueError = false,
                showDuplicateKeyError = false,
                newItem = newItem.copy(variants = newItem.variants + newItemVariant),
                newItemVariant = DisplayItem.Variant(),
                optionValue = ""
            )
        } else {
            copy(
                displayAddOptionView = false,
                showAddOptionError = false,
                showValueError = false,
                showDuplicateKeyError = false,
                newItemVariant = DisplayItem.Variant(),
                optionValue = ""
            )
        }
    }

    /**
     * Attempts to add a new variant value. Returns new state with either:
     * - The value added successfully, or
     * - Appropriate error flags set if validation fails
     */
    fun addVariantValue(): AddItemState {
        if (newItemVariant.key.isBlank()) {
            return copy(showAddOptionError = true)
        }

        if (newItemVariant.valueList.isEmpty()) {
            val isDuplicateKey = newItem.variants.any {
                it.key.equals(newItemVariant.key.trim(), ignoreCase = true)
            }
            if (isDuplicateKey) {
                return copy(showDuplicateKeyError = true)
            }
        }

        if (optionValue.isBlank()) {
            return copy(showValueError = true)
        }

        if (newItemVariant.valueList.contains(optionValue.trim())) {
            return copy(showValueError = true)
        }

        return copy(
            newItemVariant = newItemVariant.copy(
                valueList = newItemVariant.valueList + optionValue.trim()
            ),
            optionValue = "",
            showValueError = false,
            showDuplicateKeyError = false
        )
    }

    companion object {
        /**
         * Creates an AddItemState from an optional initial item (for edit mode)
         */
        fun fromItem(item: DisplayItem?): AddItemState {
            val displayItem = item ?: DisplayItem()
            return AddItemState(
                newItem = displayItem,
                displayPrice = if (displayItem.price > 0.0) displayItem.price.toString() else "",
                isEditMode = item != null
            )
        }
    }
}
