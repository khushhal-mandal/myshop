package com.example.myshoppinguserapp.presentation.screen.product

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.myshoppinguserapp.domain.model.Cart

@Composable
fun ProductDetailScreenUI(
    viewModel: ProductViewModel = hiltViewModel(),
    productId: String
) {
    val productDetailsState by viewModel.getProductByIdState.collectAsState()
    val addToCartState by viewModel.addToCartState.collectAsState()
    val addToWishListState by viewModel.addToWishListState.collectAsState()
    val removeWishMessage by viewModel.removeMessage.collectAsState()

    val context = LocalContext.current

    val sizes = listOf("XS", "S", "M", "L", "XL")
    var selectedSize by remember { mutableStateOf(sizes.first()) }

    val colors = listOf(
        "#E57373" to Color(0xFFE57373), // Red
        "#4DB6AC" to Color(0xFF4DB6AC), // Teal
        "#81C784" to Color(0xFF81C784), // Green
        "#FFF176" to Color(0xFFFFF176)  // Yellow
    )
    var selectedColor by remember { mutableStateOf(colors.first()) }

    var quantity by remember { mutableIntStateOf(1) }
    var isWishlisted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getProductById(productId)
    }

    LaunchedEffect(addToCartState) {
        addToCartState?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearAddToCartState()
        }
    }

    LaunchedEffect(addToWishListState) {
        addToWishListState?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearAddToWishListState()
        }
    }

    LaunchedEffect(removeWishMessage) {
        removeWishMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearRemoveMessage()
        }
    }

    when {
        productDetailsState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...")
            }
        }

        productDetailsState.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${productDetailsState.error}")
            }
        }

        productDetailsState.product != null -> {
            val product = productDetailsState.product!!
            val discount = ((product.price.toInt() - product.finalPrice.toInt()) * 100 / product.price.toInt())

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = product.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Row(Modifier.padding(horizontal = 16.dp)) {
                    Text("Rs. ${product.finalPrice}", color = Color.Red, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rs. ${product.price}",
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("($discount% OFF)", color = Color.Green)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Size", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp))
                Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    sizes.forEach { size ->
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .border(2.dp, if (size == selectedSize) Color.Red else Color.Gray, RoundedCornerShape(6.dp))
                                .clickable { selectedSize = size }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(size)
                        }
                    }
                }

                Text("Color", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp))
                Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    colors.forEach { (hex, color) ->
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(color)
                                .border(
                                    2.dp,
                                    if (selectedColor.first == hex) Color.Black else Color.Transparent,
                                    RoundedCornerShape(50)
                                )
                                .clickable { selectedColor = hex to color }
                        )
                    }
                }

                Text("Quantity", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(quantity.toString(), modifier = Modifier.padding(horizontal = 16.dp))
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase", modifier = Modifier.size(30.dp).clickable { quantity++ })
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease", modifier = Modifier.size(30.dp).clickable { if (quantity > 1) quantity-- })
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val unitPrice = product.finalPrice.toIntOrNull() ?: 0
                        val totalPrice = (quantity * unitPrice).toString()

                        viewModel.addToCart(
                            Cart(
                                productId = productId,
                                productName = product.name,
                                productImage = product.image,
                                size = selectedSize,
                                color = selectedColor.first,
                                quantity = quantity.toString(),
                                totalPrice = totalPrice,
                                category = product.category
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Add to Cart")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val unitPrice = product.finalPrice.toIntOrNull() ?: 0
                        val totalPrice = (quantity * unitPrice).toString()

                        if (isWishlisted) {
                            viewModel.removeProductFromWishList(productId)
                        } else {
                            viewModel.addToWishList(
                                Cart(
                                    productId = productId,
                                    productName = product.name,
                                    productImage = product.image,
                                    totalPrice = totalPrice,
                                    size = selectedSize,
                                    color = selectedColor.first,
                                    quantity = quantity.toString(),
                                    category = product.category
                                )
                            )
                        }
                        isWishlisted = !isWishlisted
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isWishlisted) Color.Red else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isWishlisted) "Wishlisted" else "Add to Wishlist")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Description", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(16.dp))
                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Text(
                    text = "Note: Color may vary slightly due to lighting and screen resolution.",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
