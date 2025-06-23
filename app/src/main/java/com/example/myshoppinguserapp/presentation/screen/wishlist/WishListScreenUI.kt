package com.example.myshoppinguserapp.presentation.screen.wishlist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myshoppinguserapp.domain.model.Cart
import com.example.myshoppinguserapp.presentation.navigation.Routes

@Composable
fun WishListScreenUI(
    viewModel: WishListViewModel = hiltViewModel(),
    navController: NavController
) {
    val wishListState by viewModel.wishListState.collectAsState()
    val removeMessage by viewModel.removeMessage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(removeMessage) {
        removeMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearRemoveMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "My Wishlist",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )

        when {
            wishListState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            wishListState.error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Error: ${wishListState.error}")
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.getWishListProducts() }) {
                        Text("Retry")
                    }
                }
            }

            wishListState.data.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No items in wishlist.")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(wishListState.data, key = { it.productId }) { item ->
                        WishListItem(
                            item = item,
                            onRemove = { viewModel.removeProductFromWishList(it) },
                            onClick = { navController.navigate(Routes.ProductDetailsScreen(item.productId)) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun WishListItem(
    item: Cart,
    onRemove: (productId: String) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(
            onClick = onClick
        ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.productImage,
                contentDescription = item.productName,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Size: ${item.size}", color = MaterialTheme.colorScheme.onPrimary)
                    Text("Qty: ${item.quantity}", color = MaterialTheme.colorScheme.onPrimary)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Color: ", color = MaterialTheme.colorScheme.onPrimary)
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .padding(start = 4.dp)
                            .clip(CircleShape)
                            .background(
                                try {
                                    Color(item.color.toColorInt())
                                } catch (e: Exception) {
                                    Color.Gray
                                }
                            )
                            .border(1.dp, Color.Black, CircleShape)
                    )
                }

                Text(
                    text = "₹${item.totalPrice}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }


            Spacer(modifier = Modifier.width(8.dp))

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.End
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Remove",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onRemove(item.productId) },
                    tint = Color.Black
                )
            }
        }
    }
}

