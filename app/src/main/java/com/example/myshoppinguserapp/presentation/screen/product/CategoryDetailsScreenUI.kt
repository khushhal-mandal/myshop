package com.example.myshoppinguserapp.presentation.screen.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myshoppinguserapp.domain.model.Product
import com.example.myshoppinguserapp.presentation.navigation.Routes
import com.example.myshoppinguserapp.ui.theme.Primary

@Composable
fun CategoryDetailsScreenUI(
    viewModel: ProductViewModel = hiltViewModel(),
    category: String,
    navController: NavController
) {
    val state by viewModel.getProductsByCategoryState.collectAsState()

    LaunchedEffect(category) {
        viewModel.getProductsByCategory(category)
    }

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${state.error}")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = category,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(state.products) { product ->
                    ProductItemCard(product = product, onClick = {
                        navController.navigate(Routes.ProductDetailsScreen(product.id))
                    })
                }
            }
        }
    }
}


@Composable
fun ProductItemCard(product: Product, onClick: () -> Unit) {
    val discount = try {
        ((product.price.toInt() - product.finalPrice.toInt()) * 100 / product.price.toInt())
    } catch (e: Exception) {
        0
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = product.description,
                maxLines = 2,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "₹${product.finalPrice}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "₹${product.price}",
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.LineThrough,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (discount > 0) {
                    Text(
                        text = "$discount% OFF",
                        fontSize = 14.sp,
                        color = Color(0xFF388E3C),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
