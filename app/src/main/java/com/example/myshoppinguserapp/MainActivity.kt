package com.example.myshoppinguserapp

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myshoppinguserapp.domain.model.Order
import com.example.myshoppinguserapp.presentation.screen.cart.CartViewModel
import com.example.myshoppinguserapp.presentation.screen.splash.MyShop
import com.example.myshoppinguserapp.ui.theme.MyShoppingUserAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultWithDataListener {

    @Inject
    lateinit var auth: FirebaseAuth

    lateinit var cartViewModel: CartViewModel

    var tempOrder: Order? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Checkout.preload(applicationContext)

        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        setContent {
            MyShoppingUserAppTheme {
                MyShop(auth)
            }
        }
    }

    fun startPayment(amount: Int) {
        val activity: Activity = this
        val co = Checkout()
        //Add Your Key Here
        co.setKeyID("add_your_razorpay_key")

        try {
            val options = JSONObject().apply {
                put("name", "MyShop")
                put("description", "Demoing Charges")
                put("image", "http://example.com/image/rzp.jpg")
                put("theme.color", "#F68B8B")
                put("currency", "INR")
                put("amount", amount)
                put("retry", JSONObject().apply {
                    put("enabled", true)
                    put("max_count", 4)
                })
                put("prefill", JSONObject().apply {
                    put("email", "myshop@example.com")
                    put("contact", "9876543210")
                })
            }

            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()

        tempOrder?.let { order ->
            cartViewModel.placeOrder(order)
            observeOrderState()
        } ?: Toast.makeText(this, "Order is empty", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this, p1 ?: "Payment failed", Toast.LENGTH_LONG).show()
    }

    private fun observeOrderState() {
        lifecycleScope.launch {
            cartViewModel.placeOrderState.collect { state ->
                when {
                    state.isLoading -> {

                    }
                    state.successMessage != null -> {
                        Toast.makeText(this@MainActivity, "Order Placed!", Toast.LENGTH_SHORT).show()
                        tempOrder = null
                        cartViewModel.clearCart()
                    }
                    state.error != null -> {
                        Toast.makeText(this@MainActivity, "Error: ${state.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
