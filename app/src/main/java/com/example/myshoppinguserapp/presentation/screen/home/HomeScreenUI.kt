package com.example.myshoppinguserapp.presentation.screen.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myshoppinguserapp.presentation.navigation.Routes
import com.example.myshoppinguserapp.ui.theme.Primary

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenUI(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val categoriesState by viewModel.getAllCategoriesState.collectAsState()
    val productsState by viewModel.getAllProductsState.collectAsState()
    val bannersState by viewModel.getBannerState.collectAsState()
    val searchResults by viewModel.searchProductState.collectAsState()


    var searchText by remember { mutableStateOf("") }

    val isLoading = categoriesState.isLoading || productsState.isLoading || bannersState.isLoading
    val isError = categoriesState.error ?: productsState.error ?: bannersState.error

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...")
            }
            return
        }

        isError != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $isError")
            }
            return
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        // Search + Notification
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.searchProducts(it)
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
                },
                label = { Text("Search") },
                modifier = Modifier.weight(1f)
            )


            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Outlined.LocalShipping,
                contentDescription = "My Orders",
                modifier = Modifier.size(26.dp)
                    .clickable(
                        onClick = {
                            navController.navigate(Routes.OrderScreen)
                        }
                    )
            )
        }

        when {
            searchResults.isLoading -> {
                Text("Searching...", modifier = Modifier.padding(12.dp))
            }

            searchText.isNotBlank() && searchResults.products.isEmpty() -> {
                Text("No products found", modifier = Modifier.padding(12.dp))
            }

            searchText.isBlank() -> {
                viewModel.resetSearchResult()
            }

            searchResults.products.isNotEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    searchResults.products.forEach { product ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(Routes.ProductDetailsScreen(product.id))
                                    searchText = ""
                                }
                                .padding(12.dp)
                        ) {
                            AsyncImage(
                                model = product.image,
                                contentDescription = product.name,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Rs: ${product.finalPrice}",
                                    color = Primary,
                                    fontSize = 14.sp
                                )
                            }

                            // Discount label
                            val discount = (((product.price.toFloat() - product.finalPrice.toFloat()) / product.price.toFloat()) * 100).toInt()
                            if (discount > 0) {
                                Text(
                                    text = "$discount% OFF",
                                    color = Color(0xFF388E3C),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        // Categories
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Categories")
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "See All",
                modifier = Modifier.clickable {
                    navController.navigate(Routes.CategoriesScreen)
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(modifier = Modifier.height(100.dp)) {
            items(categoriesState.categories) { category ->
                Column(
                    modifier = Modifier
                        .width(80.dp)
                        .padding(horizontal = 8.dp)
                        .clickable {
                            navController.navigate(Routes.CategoryDetailsScreen(category.name))
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(64.dp)
                    ) {
                        AsyncImage(
                            model = category.image,
                            contentDescription = category.name,
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(2.dp, Color.Black, CircleShape)
                                .padding(8.dp)
                        )
                    }

                    Text(
                        text = category.name,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 4.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Featured")
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            items(bannersState.banners) { banner ->
                Box(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .width(300.dp)
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                        .clickable(
                            onClick = {
                                navController.navigate(Routes.AllProductScreen)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = banner.image,
                        contentDescription = "Banner Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Products
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Flash Sale")
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "See All",
                modifier = Modifier.clickable {
                    navController.navigate(Routes.AllProductScreen)
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            items(productsState.products) { product ->
                Column(
                    modifier = Modifier
                        .width(200.dp)
                        .padding(8.dp)
                        .clickable {
                            navController.navigate(Routes.ProductDetailsScreen(product.id))
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = product.image,
                            contentDescription = "Product Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .border(2.dp, Color.Black, RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = product.name)
                    Text(text = "Rs: ${product.finalPrice}", color = Primary)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Rs: ${product.price}",
                            color = Color.Gray,
                            style = TextStyle(textDecoration = TextDecoration.LineThrough)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val discount = (((product.price.toFloat() - product.finalPrice.toFloat()) / product.price.toFloat()) * 100).toInt()
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
}