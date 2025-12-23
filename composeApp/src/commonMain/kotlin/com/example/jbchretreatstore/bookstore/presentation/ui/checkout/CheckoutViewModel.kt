package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.usecase.CheckoutUseCase
import com.example.jbchretreatstore.bookstore.domain.usecase.ManageCartUseCase
import com.example.jbchretreatstore.bookstore.presentation.CheckoutState
import com.example.jbchretreatstore.bookstore.presentation.shared.CartStateHolder
import com.example.jbchretreatstore.bookstore.presentation.shared.SnackbarManager
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_failed
import jbchretreatstore.composeapp.generated.resources.checkout_success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Checkout screen following MVI architecture pattern.
 * All user actions are processed through the handleIntent() function.
 */
class CheckoutViewModel(
    private val manageCartUseCase: ManageCartUseCase,
    private val checkoutUseCase: CheckoutUseCase,
    private val cartStateHolder: CartStateHolder,
    private val snackbarManager: SnackbarManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())

    val uiState = combine(
        _uiState,
        cartStateHolder.cartState
    ) { state, cart ->
        state.copy(
            checkoutItems = cart.checkoutList,
            totalPrice = cart.checkoutList.sumOf { it.totalPrice }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CheckoutUiState()
    )

    /**
     * Central intent handler following MVI pattern.
     * All user actions should be dispatched through this function.
     */
    fun handleIntent(intent: CheckoutIntent) {
        when (intent) {
            is CheckoutIntent.SelectPaymentMethod -> reducePaymentMethod(intent.paymentMethod)
            is CheckoutIntent.ProcessCheckout -> handleProcessCheckout(intent.buyerName)
            is CheckoutIntent.RemoveFromCart -> handleRemoveFromCart(intent.item)
            is CheckoutIntent.ShowCheckoutDialog -> reduceShowCheckoutDialog(intent.show)
            is CheckoutIntent.CheckoutSuccessHandled -> reduceCheckoutSuccessHandled()
        }
    }

    // region State Reducers (pure state updates)

    private fun reducePaymentMethod(paymentMethod: PaymentMethod) {
        _uiState.update { it.copy(selectedPaymentMethod = paymentMethod) }
    }

    private fun reduceShowCheckoutDialog(show: Boolean) {
        _uiState.update { it.copy(showCheckoutDialog = show) }
    }

    private fun reduceCheckoutSuccessHandled() {
        _uiState.update { it.copy(checkoutSuccess = false) }
    }

    // endregion

    // region Side Effects (async operations)

    private fun handleRemoveFromCart(checkoutItem: CheckoutItem) {
        val result = manageCartUseCase.removeFromCart(
            cartStateHolder.cartState.value,
            checkoutItem
        )
        result.onSuccess { updatedCart ->
            cartStateHolder.updateCart(updatedCart)
        }
    }

    private fun handleProcessCheckout(buyerName: String) {
        viewModelScope.launch {
            val checkoutState = CheckoutState(
                buyerName = buyerName,
                checkoutStatus = CheckoutStatus.CHECKED_OUT,
                paymentMethod = _uiState.value.selectedPaymentMethod
            )

            val result = checkoutUseCase.processCheckout(
                cartStateHolder.cartState.value,
                checkoutState
            )

            result.onSuccess {
                // Reset cart after successful checkout
                cartStateHolder.clearCart()
                _uiState.update {
                    it.copy(
                        showCheckoutDialog = false,
                        selectedPaymentMethod = PaymentMethod.CASH,
                        checkoutSuccess = true
                    )
                }
                snackbarManager.showSnackbar(Res.string.checkout_success)
            }.onFailure { error ->
                println("Checkout failed: ${error.message}")
                _uiState.update { it.copy(showCheckoutDialog = false) }
                snackbarManager.showSnackbar(Res.string.checkout_failed)
            }
        }
    }

    // endregion

    fun isCartEmpty(): Boolean = cartStateHolder.cartState.value.checkoutList.isEmpty()
}

