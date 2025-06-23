package com.example.myshoppinguserapp.presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myshoppinguserapp.domain.model.Product
import com.example.myshoppinguserapp.presentation.navigation.Routes
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import com.example.myshoppinguserapp.ui.theme.Primary

@Composable
fun AllProductsScreenUI(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val productsState = viewModel.getAllProductsState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.getAllProducts()
    }

    when {
        productsState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...")
            }
        }

        productsState.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${productsState.error}")
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Sale",
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 12.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productsState.products) { product ->
                        ProductCard(product = product) {
                            navController.navigate(Routes.ProductDetailsScreen(product.id))
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Primary)

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = product.description, maxLines = 2, color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "₹${product.finalPrice}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
