package com.example.myshoppinguserapp.presentation.screen.cart

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.myshoppinguserapp.presentation.navigation.Routes

@Composable
fun CartScreenUI(
    viewModel: CartViewModel = hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.getCartState.collectAsState()
    val removeMessage = viewModel.removeFromCartState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getCart()
    }

    LaunchedEffect(removeMessage.value) {
        removeMessage.value?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearRemoveFromCartState()
        }
    }

    when {
        state.value.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }

        state.value.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${state.value.error}")
            }
            return
        }

        state.value.cart.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Your cart is empty.")
            }
            return
        }
    }

    val cartItems = state.value.cart
    val subtotal = cartItems.sumOf { it.totalPrice.toIntOrNull() ?: 0 }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Shopping Cart", modifier = Modifier.padding(16.dp), fontSize = 22.sp)
        Text("Items", modifier = Modifier.padding(start = 16.dp), fontSize = 16.sp)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cartItems) { item ->
                val colorHex = item.color
                val displayColor = try {
                    Color(colorHex.toColorInt())
                } catch (e: Exception) {
                    Color.Gray
                }

                Column(modifier = Modifier.fillMaxWidth()
                    .clickable(
                        onClick = {navController.navigate(Routes.ProductDetailsScreen(item.productId))}
                    )) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = item.productImage,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.productName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Size: ${item.size}", fontSize = 14.sp)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Color: ", fontSize = 14.sp)
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(start = 4.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(displayColor)
                                        .border(1.dp, Color.Black, RoundedCornerShape(50))
                                )
                            }
                            Text("Qty: ${item.quantity}", fontSize = 14.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Rs: ${item.totalPrice}", fontSize = 14.sp, color = Color.Black)
                            IconButton(onClick = { viewModel.removeFromCart(item.productId) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = Color.Black
                                )
                            }
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                }
            }
        }

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Subtotal", fontWeight = FontWeight.Bold)
            Text("Rs: $subtotal", fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = {
                navController.navigate(Routes.CheckoutScreen(
                    totalPrice = subtotal
                ))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Checkout")
        }
    }
}
