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

    fun onPaymentMethodSelected(paymentMethod: PaymentMethod) {
        _uiState.update { it.copy(selectedPaymentMethod = paymentMethod) }
    }

    fun onRemoveFromCart(checkoutItem: CheckoutItem) {
        val result = manageCartUseCase.removeFromCart(
            cartStateHolder.cartState.value,
            checkoutItem
        )
        result.onSuccess { updatedCart ->
            cartStateHolder.updateCart(updatedCart)
        }
    }

    fun showCheckoutDialog(show: Boolean) {
        _uiState.update { it.copy(showCheckoutDialog = show) }
    }

    fun processCheckout(buyerName: String) {
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

    /**
     * Reset checkout success state after navigation is handled
     */
    fun onCheckoutSuccessHandled() {
        _uiState.update { it.copy(checkoutSuccess = false) }
    }

    fun isCartEmpty(): Boolean = cartStateHolder.cartState.value.checkoutList.isEmpty()
}

