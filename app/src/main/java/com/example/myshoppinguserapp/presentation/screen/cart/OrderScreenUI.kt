package com.example.myshoppinguserapp.presentation.screen.cart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OrdersScreenUI(
    viewModel: CartViewModel = hiltViewModel()
) {
    val state = viewModel.orderState.collectAsState().value

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Your Orders", fontWeight = FontWeight.Bold, fontSize = 22.sp)

        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }

            state.error != null -> {
                Text("Error: ${state.error}")
            }

            state.orders.isEmpty() -> {
                Text("You have no orders.")
            }

            else -> {
                LazyColumn {
                    items(state.orders) { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Order ID: ${order.orderId}", fontWeight = FontWeight.SemiBold)
                                Text("Total: ₹${order.totalPrice}")
                                Text("Items: ${order.products.size}")
                                Spacer(modifier = Modifier.height(8.dp))
                                order.products.forEach {
                                    Text("- ${it.productName} x ${it.quantity}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
