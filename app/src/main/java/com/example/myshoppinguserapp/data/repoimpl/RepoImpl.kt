package com.example.myshoppinguserapp.data.repoimpl

import android.net.Uri
import com.example.myshoppinguserapp.common.BANNER
import com.example.myshoppinguserapp.common.CART
import com.example.myshoppinguserapp.common.CATEGORY
import com.example.myshoppinguserapp.common.PRODUCT
import com.example.myshoppinguserapp.common.USER
import com.example.myshoppinguserapp.common.USER_IMAGES
import com.example.myshoppinguserapp.common.WISHLIST
import com.example.myshoppinguserapp.common.resultstate.ResultState
import com.example.myshoppinguserapp.domain.model.Banner
import com.example.myshoppinguserapp.domain.model.Cart
import com.example.myshoppinguserapp.domain.model.Category
import com.example.myshoppinguserapp.domain.model.Order
import com.example.myshoppinguserapp.domain.model.Product
import com.example.myshoppinguserapp.domain.model.User
import com.example.myshoppinguserapp.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : Repo {

    override fun getAllCategories(): Flow<ResultState<List<Category>>> = callbackFlow {
        trySend(ResultState.Loading)

        firestore.collection(CATEGORY).get()
            .addOnSuccessListener { list ->
                val categories = list.mapNotNull { document ->
                    val category = document.toObject(Category::class.java)
                    category.copy(id = document.id)
                }
                trySend(ResultState.Success(categories))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }

        awaitClose { close() }
    }


    override fun getAllProducts(): Flow<ResultState<List<Product>>> = callbackFlow {
        trySend(ResultState.Loading)

        firestore.collection(PRODUCT).get()
            .addOnSuccessListener { list ->
                val products = list.mapNotNull { document ->
                    val product = document.toObject(Product::class.java)
                    product.copy(id = document.id)
                }
                trySend(ResultState.Success(products))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }

        awaitClose { close() }
    }


    override fun registerUser(user: User): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnSuccessListener {
                firestore.collection(USER).document(it.user?.uid.toString()).set(user)
                    .addOnSuccessListener {
                        trySend(ResultState.Success("User registered successfully"))
                    }
                    .addOnFailureListener {
                        trySend(ResultState.Error(it.toString()))
                    }
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }

        awaitClose { close() }
    }


    override fun loginUser(
        email: String,
        password: String
    ): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                trySend(ResultState.Success("User logged in successfully"))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }

        awaitClose { close() }
    }


    override fun uploadImage(imageUri: Uri): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val imageRef = storage.reference.child("$USER_IMAGES/${System.currentTimeMillis()}")
        val uploadTask = imageRef.putFile(imageUri)
        val userId = auth.currentUser?.uid
        if (userId == null || userId.isEmpty()) {
            trySend(ResultState.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        uploadTask
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl
                    .addOnSuccessListener { imageUri ->
                        val imageUrl = imageUri.toString()
                        firestore.collection(USER)
                            .document(userId)
                            .update("image", imageUrl)
                            .addOnSuccessListener {
                                trySend(ResultState.Success(imageUrl))
                                close()
                            }
                            .addOnFailureListener { e ->
                                trySend(ResultState.Error("Failed to update Firestore: ${e.message}"))
                                close()
                            }
                    }
                    .addOnFailureListener { e ->
                        trySend(ResultState.Error("Failed to get download URL: ${e.message}"))
                        close()
                    }
            }
            .addOnFailureListener { e ->
                trySend(ResultState.Error("Image upload failed: ${e.message}"))
                close()
            }

        awaitClose { close() }
    }


    override fun getProductById(productId: String): Flow<ResultState<Product>> = callbackFlow {
        trySend(ResultState.Loading)

        firestore.collection(PRODUCT).document(productId).get()
            .addOnSuccessListener {
                val product = it.toObject(Product::class.java)
                trySend(ResultState.Success(product!!))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }

        awaitClose { close() }
    }


    override fun getUserData(): Flow<ResultState<User>> = callbackFlow {
        trySend(ResultState.Loading)

        val userId = auth.currentUser?.uid.orEmpty()

        firestore.collection(USER).document(userId).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if (user != null) trySend(ResultState.Success(user))
                else trySend(ResultState.Error("User not found"))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }

        awaitClose { close() }
    }


    override fun updateUserData(user: User): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val uid = auth.currentUser?.uid.orEmpty()

        val updatedFields = mapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "email" to user.email,
            "phone" to user.phone,
            "address" to user.address
        )

        firestore.collection(USER).document(uid)
            .update(updatedFields)
            .addOnSuccessListener {
                trySend(ResultState.Success("User data updated successfully"))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }

        awaitClose { close() }
    }


    override fun logoutUser(): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        auth.signOut()
        trySend(ResultState.Success("User logged out successfully"))

        awaitClose { close() }
    }


    override fun addProductToCart(cart: Cart): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw Exception("User not logged in")


        firestore
            .collection("CART")
            .document(userId)
            .collection("PRODUCTS")
            .document(cart.productId)
            .set(cart)
            .await()

        emit(ResultState.Success("Added to cart"))
    }.catch {
        emit(ResultState.Error(it.message ?: "Something went wrong"))
    }


    override fun getCartProducts(): Flow<ResultState<List<Cart>>> = callbackFlow {
        trySend(ResultState.Loading)

        val uid = auth.currentUser?.uid.orEmpty()

        if (uid.isEmpty()) {
            trySend(ResultState.Error("User not authenticated"))
            close()
            return@callbackFlow
        }

        firestore.collection(CART)
            .document(uid)
            .collection("PRODUCTS")
            .get()
            .addOnSuccessListener { result ->
                val cartItems = result.map { document ->
                    document.toObject(Cart::class.java)
                }
                trySend(ResultState.Success(cartItems))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.localizedMessage ?: "Unknown error occurred"))
            }

        awaitClose { close() }
    }


    override fun removeProductFromCart(productId: String): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)

            val userId = auth.currentUser?.uid.orEmpty()
            firestore.collection(CART).document(userId)
                .collection("PRODUCTS").document(productId).delete()
                .addOnSuccessListener {
                    trySend(ResultState.Success("Removed from cart"))
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.localizedMessage ?: "Unknown error occurred"))
                }
            awaitClose { close() }
        }


    override fun addToWishList(wish: Cart): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val userId = auth.currentUser?.uid.orEmpty()

        firestore.collection("WISHLIST").document(userId)
            .collection("PRODUCTS").document(wish.productId).set(wish)
            .addOnSuccessListener {
                trySend(ResultState.Success("Added to wishlist"))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.localizedMessage ?: "Unknown error occurred"))
            }
        awaitClose { close() }
    }


    override fun getWishListProducts(): Flow<ResultState<List<Cart>>> = callbackFlow {
        trySend(ResultState.Loading)
        val uid = auth.currentUser?.uid.orEmpty()

        if (uid.isEmpty()) {
            trySend(ResultState.Error("User not authenticated"))
            close()
            return@callbackFlow
        }

        firestore.collection("WISHLIST")
            .document(uid)
            .collection("PRODUCTS")
            .get()
            .addOnSuccessListener { result ->
                val cartItems = result.mapNotNull { it.toObject(Cart::class.java) }
                trySend(ResultState.Success(cartItems))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.localizedMessage ?: "Unknown error occurred"))
            }

        awaitClose { close() }
    }


    override fun getBanners(): Flow<ResultState<List<Banner>>> = callbackFlow {
        trySend(ResultState.Loading)

        firestore.collection(BANNER).get()
            .addOnSuccessListener { list ->
                val banners = list.mapNotNull { document ->
                    document.toObject(Banner::class.java)
                }
                trySend(ResultState.Success(banners))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }

        awaitClose { close() }
    }


    override fun searchProducts(query: String): Flow<ResultState<List<Product>>> = callbackFlow {
        trySend(ResultState.Loading)

        firestore.collection(PRODUCT).orderBy("name")
            .startAt(query).get()
            .addOnSuccessListener {
                val products = it.mapNotNull { document ->
                    document.toObject(Product::class.java)
                }
                trySend(ResultState.Success(products))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }

        awaitClose { close() }
    }


    override fun removeProductFromWishList(productId: String): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)

            val userId = auth.currentUser?.uid.orEmpty()
            firestore.collection(WISHLIST).document(userId)
                .collection("PRODUCTS").document(productId).delete()
                .addOnSuccessListener {
                    trySend(ResultState.Success("Removed from wishlist"))
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.localizedMessage ?: "Unknown error occurred"))
                }

            awaitClose { close() }
        }


    override fun getProductsByCategory(category: String): Flow<ResultState<List<Product>>> =
        callbackFlow {
            trySend(ResultState.Loading)

            firestore.collection(PRODUCT)
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener { list ->
                    val products = list.mapNotNull { document ->
                        document.toObject(Product::class.java)
                    }
                    trySend(ResultState.Success(products))
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }

            awaitClose { close() }
        }


    override fun placeOrder(order: Order): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(ResultState.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        val orderRef = firestore.collection("USERS")
            .document(uid)
            .collection("ORDERS")
            .document(order.orderId)

        orderRef.set(order)
            .addOnSuccessListener {
                trySend(ResultState.Success(order.orderId))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.message ?: "Unknown error"))
                close()
            }

        awaitClose {}
    }


    override suspend fun clearCart() {
        val userId = auth.currentUser?.uid.orEmpty()
        val cartCollection = firestore.collection(CART)
            .document(userId)
            .collection("PRODUCTS")

        val cartItems = cartCollection.get().await()
        for (doc in cartItems.documents) {
            doc.reference.delete().await()
        }
    }


    override suspend fun getOrders(userId: String): List<Order> {
        return firestore.collection("USERS")
            .document(userId)
            .collection("ORDERS")
            .get()
            .await()
            .documents.mapNotNull { it.toObject(Order::class.java) }
    }
}