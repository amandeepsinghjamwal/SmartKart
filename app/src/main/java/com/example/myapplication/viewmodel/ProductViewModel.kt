package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.room.Transaction
import com.example.myapplication.ApplicationClass
import com.example.myapplication.api.ApplicationApi
import com.example.myapplication.api.models.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel : ViewModel() {
    //    fun getProduct():LiveData<List<Product>> = productDao.getData()
    var _productList = MutableLiveData<List<ProductDetails>>()
    val productList: LiveData<List<ProductDetails>>
        get() = _productList

    var db = Firebase.firestore

    private var firebaseTotal: Double = 0.0

    val moshi = Moshi.Builder().build()

    var _wishlistProducts = MutableLiveData<List<WishlistResponseData>>()
    val wishlistProducts: LiveData<List<WishlistResponseData>> get() = _wishlistProducts

    var _cartResponseData = MutableLiveData<List<CartResponseData>>()
    val cartResponseData: LiveData<List<CartResponseData>> get() = _cartResponseData

    var _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> get() = _totalPrice

    var _orderedProducts = MutableLiveData<List<OrderHistoryProductsData>>()
    val orderedProducts: LiveData<List<OrderHistoryProductsData>> get() = _orderedProducts

    /**
     * Loads product data in mainfragment (HomeScreen)
     * */
    fun loadProductData(): LiveData<Int> {
        val _response = MutableLiveData<Int>()
        val response: LiveData<Int> = _response
        if (ApplicationClass.sharedPreferences!!.contains("JWTtoken")) {
            viewModelScope.launch(Dispatchers.IO) {
                val tokenJWT =
                    ApplicationClass.sharedPreferences!!.getString("JWTtoken", null).toString()
                Log.e("JWTTOKEN", tokenJWT)
                Log.e(
                    "Email",
                    ApplicationClass.sharedPreferences!!.getString("Email", null).toString()
                )
                val callApi = ApplicationApi.retrofitService.getAllProducts("Bearer $tokenJWT")
                callApi.enqueue(object : Callback<ProductResponseData> {
                    override fun onResponse(
                        call: Call<ProductResponseData>,
                        response: Response<ProductResponseData>
                    ) {
                        Log.e("tokem", response.code().toString())
                        _response.value = response.code()
                        if (response.code() == 200) {
                            _response.value = response.code()
//                            _productList.value = response.body()!!.data
                            getFirebaseProducts(response.body()!!.data)
                            Log.e("RESPONSEDATA", response.body()!!.data.toString())
                        }

                    }

                    override fun onFailure(call: Call<ProductResponseData>, t: Throwable) {
                        Log.e("RESPONSEDATA", "FAILURE")
                        _response.value = 0
                    }
                })
            }
        }
        return response
    }

    private fun getFirebaseProducts(data: List<ProductDetails>) {
        val tempList = mutableListOf<ProductDetails>()
        db.collection("products").get().addOnSuccessListener { snapshot ->

            for (document in snapshot) {
//                Log.e("Data received", document.data["all_products"].toString())
                val productList = document?.get("all_products") as List<Map<String, Any>>
                val products = productList.map {
                    ProductDetails(
                        _id = it["_id"] as String,
                        description = it["description"] as String,
                        imageUrl = it["imageUrl"] as String,
                        price = (it["price"] as String),  // Assuming the price is stored as a double
                        title = it["title"] as String,
                        isFirebaseProduct = it["isFirebaseProduct"] as Boolean
                    )
                }
                val userId = ApplicationClass.sharedPreferences?.getString("userId", "")
                val userDocRef: DocumentReference =
                    db.collection("users").document(userId ?: "User")
                db.runTransaction { transaction ->
                    val userDoc = transaction.get(userDocRef)
                    if (userDoc.exists()) {
                        userDocRef.get().addOnSuccessListener {
                            try {
                                val result = it.data?.get("cart") as List<HashMap<Any, Any>>
                                result.forEach { map ->
                                    val productDetails = map["product"] as Map<*, *>
                                    products.forEach { prod ->
                                        if (prod._id == productDetails["_id"]) {
                                            prod.cartItemId = "firebaseCart"
                                            prod.isInCart = true
                                            return@forEach
                                        }
                                    }
                                }
                                tempList.addAll(products)
                                tempList.addAll(data)
                                _productList.value = tempList.shuffled()
                            } catch (e: Exception) {
                                tempList.addAll(products)
                                tempList.addAll(data)
                                _productList.value = tempList.shuffled()
                                Log.e("exc", e.message.toString())
                            }
                        }.addOnFailureListener {
                            tempList.addAll(products)
                            tempList.addAll(data)
                            _productList.value = tempList.shuffled()
                            Log.e("exc", it.message.toString())
                        }
                    } else {
                        tempList.addAll(products)
                        tempList.addAll(data)
                        _productList.value = tempList.shuffled()
                        Log.e("exc", _productList.value.toString())
                    }
                }

                /* tempList.addAll(products)
                 tempList.addAll(data)
                 _productList.value = tempList.shuffled()*/

            }

        }.addOnFailureListener {
            tempList.addAll(data)
            _productList.value = tempList.shuffled()
            Log.e("Data received", it.toString())
        }.addOnCanceledListener {
            tempList.addAll(data)
            _productList.value = tempList.shuffled()
            Log.e("Data received", "it.toString()")
        }
    }

    /**
     * loads wishlist data
     * */
    fun loadWishlistData() {
        if (ApplicationClass.sharedPreferences!!.contains("JWTtoken")) {
            val tokenJWT =
                ApplicationClass.sharedPreferences!!.getString("JWTtoken", null).toString()
            viewModelScope.launch(Dispatchers.IO) {
                val product: List<WishlistResponseData> = emptyList()
                val callApi = ApplicationApi.retrofitService.getWishlist("Bearer $tokenJWT")
                callApi.enqueue(object : Callback<WishlistResponse> {
                    override fun onResponse(
                        call: Call<WishlistResponse>,
                        response: Response<WishlistResponse>
                    ) {
                        if (response.code() == 200) {
                            _wishlistProducts.value = response.body()!!.data!!
                        }
                        if (response.code() == 400) {
                            Log.e("Wishlist@", product.toString())
                        }
                    }

                    override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                        Log.e("Wishlist@", product.toString() + "here " + tokenJWT)
                    }
                })
            }
        }
    }

    fun cartResponseData() {

        val data = emptyList<CartResponseData>()
        if (ApplicationClass.sharedPreferences!!.contains("JWTtoken")) {
            val tokenJWT =
                ApplicationClass.sharedPreferences!!.getString("JWTtoken", null).toString()
            viewModelScope.launch(Dispatchers.IO) {
                val callApi = ApplicationApi.retrofitService.getCartProducts("Bearer $tokenJWT")
                callApi.enqueue(object : Callback<GetCartResponse> {

                    override fun onFailure(call: Call<GetCartResponse>, t: Throwable) {
                        Log.e("CARTRESPONSE", "Failed")
                    }

                    override fun onResponse(
                        call: Call<GetCartResponse>,
                        response: Response<GetCartResponse>
                    ) {
                        if (response.code() == 200) {
                            if (response.body()!!.data != null && response.body()!!.cartTotal != null) {
                                getFirebaseCartProducts(response.body()!!.data!!)
//                                _cartResponseData.value = response.body()!!.data!!
                                _totalPrice.value = response.body()!!.cartTotal!!
                            } else {
//                                Log.e("Response body")
                                _cartResponseData.value = data
                                _totalPrice.value = 0.0
                            }
                        }
                    }
                })
            }
        }
    }

    private fun getFirebaseCartProducts(data: List<CartResponseData>) {
        val tempList = mutableListOf<CartResponseData>()
        val userId = ApplicationClass.sharedPreferences?.getString("userId", "")
        val userDocRef: DocumentReference =
            db.collection("users").document(userId ?: "User")
        db.runTransaction { transaction ->
            val userDoc = transaction.get(userDocRef)
            if (userDoc.exists()) {
                userDocRef.get().addOnSuccessListener {
                    try {
                        val firebaseProductList = mutableListOf<CartResponseData>()
                        val result = it.data?.get("cart") as List<Map<Any, Any>>

                        result.forEach { map ->
                            val productMap = map["product"] as Map<*, *>?
                            Log.e("Hereee data", map["product"].toString())
                            if (productMap != null) {
                                val product = ProductDetails(
                                    _id = productMap["_id"] as String,
                                    description = (productMap["description"] as String),
                                    imageUrl = (productMap["imageUrl"] as String),
                                    price = (productMap["price"] as String),  // Assuming the price is stored as a double
                                    title = productMap["title"] as String,
                                    isFirebaseProduct = productMap["isFirebaseProduct"] as Boolean?
                                )
                                firebaseTotal += (product.price.toDouble() + map["count"].toString()
                                    .toDouble())
                                firebaseProductList.add(
                                    CartResponseData(
                                        "null",
                                        "null",
                                        10.99,
                                        productDetails = product,
                                        quantity = 1,
                                        userId
                                    )
                                )
                            }
                        }
                        tempList.addAll(data)
                        tempList.addAll(firebaseProductList)
                        _cartResponseData.value = tempList
                        _totalPrice.value = (totalPrice.value ?: 0.0) + firebaseTotal
                    } catch (e: Exception) {
                        tempList.addAll(data)
                        _cartResponseData.value = tempList
                        Log.e("exc", e.toString())
                    }
                }.addOnFailureListener {
                    tempList.addAll(data)
                    _cartResponseData.value = tempList
                    Log.e("exc", it.message.toString())
                }
            }
        }

    }


    fun addToWishlist(pid: String?): LiveData<String> {

        val watchListObj = WatchlistRequestData(pid!!)
        val tokenJWT =
            ApplicationClass.sharedPreferences!!.getString("JWTtoken", null).toString()
        val callApi =
            ApplicationApi.retrofitService.addToWishList(watchListObj, "Bearer $tokenJWT")
        val _response = MutableLiveData<String>()
        val response: LiveData<String> = _response
        callApi.enqueue(object : Callback<AddToWishlistResponse> {
            override fun onResponse(
                call: Call<AddToWishlistResponse>,
                response: Response<AddToWishlistResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()!!.data != null) {
                        _response.value = response.body()!!.data!!._id
                    }

                } else if (response.code() == 400) {
                    _response.value = "fail"
                }
            }

            override fun onFailure(call: Call<AddToWishlistResponse>, t: Throwable) {
                _response.value = "fail"
            }
        })
        return response
    }

    fun addProductToCart(pid: String?): LiveData<String> {

        val addToCartObj = WatchlistRequestData(pid!!)
        val tokenJWT =
            ApplicationClass.sharedPreferences!!.getString("JWTtoken", null).toString()
        val callApi =
            ApplicationApi.retrofitService.addToCart(addToCartObj, "Bearer $tokenJWT")
        val _response = MutableLiveData<String>()
        val response: LiveData<String> = _response
        callApi.enqueue(object : Callback<AddtoCartResponse> {
            override fun onResponse(
                call: Call<AddtoCartResponse>,
                response: Response<AddtoCartResponse>
            ) {
                if (response.code() == 201) {
                    if (response.body()!!.data != null) {
                        _response.value = response.body()!!.data!!.cartId
                    }

                } else if (response.code() == 400) {
                    _response.value = "fail"
                }
            }

            override fun onFailure(call: Call<AddtoCartResponse>, t: Throwable) {
                _response.value = "fail"
            }
        })
        return response
    }

    fun addProductToFirebaseCart(productDetails: ProductDetails, onComplete: (Boolean) -> Unit) {

        val userId = ApplicationClass.sharedPreferences?.getString("userId", "")
        val userDocRef: DocumentReference = db.collection("users").document(userId ?: "User")

        db.runTransaction { transaction ->
            val userDoc = transaction.get(userDocRef)
            if (!userDoc.exists()) {
                val newUserDoc = hashMapOf(
                    "cart" to listOf(mapOf("product" to productDetails, "count" to 1))
                    // Add other fields as needed
                )
                transaction.set(userDocRef, newUserDoc)
            } else {
                val currentCart = userDoc.get("cart") as? List<Map<String, Any>> ?: emptyList()

                // Add the new product to the cart
                val newCart =
                    currentCart + mapOf("product" to productDetails, "count" to 1)
                // Update the cart field in the document
                transaction.update(userDocRef, "cart", newCart)
            }

            // Transaction success
            true
        }.addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            Log.e("Failure exception", it.message.toString())
            onComplete(false)
        }
    }

    fun removeFromWishlist(pid: String?): LiveData<Int> {

        val watchListObj = WatchListRemoveRequestData(pid!!)
        val tokenJWT =
            ApplicationClass.sharedPreferences!!.getString("JWTtoken", null).toString()
        val callApi =
            ApplicationApi.retrofitService.removeFromWatchlist(watchListObj, "Bearer $tokenJWT")
        val _response = MutableLiveData<Int>()
        val response: LiveData<Int> = _response
        callApi.enqueue(object : Callback<WatchListRemoveResponse> {
            override fun onResponse(
                call: Call<WatchListRemoveResponse>,
                response: Response<WatchListRemoveResponse>
            ) {
                if (response.code() == 200) {
                    _response.value = response.code()

                } else if (response.code() == 400) {
                    _response.value = response.code()
                }
            }

            override fun onFailure(call: Call<WatchListRemoveResponse>, t: Throwable) {
                _response.value = 0
            }
        })
        return response
    }

    fun removeFromCart(productId: String): LiveData<Int> {
        val _response = MutableLiveData<Int>()
        val response: LiveData<Int> = _response
        Log.e("PRODID", productId)
        val removeFromCartObj = RemoveFromCartRequestData(productId)
        val tokenJWT =
            ApplicationClass.sharedPreferences!!.getString("JWTtoken", null).toString()

        val callApi =
            ApplicationApi.retrofitService.removeFromCart(removeFromCartObj, "Bearer $tokenJWT")

        callApi.enqueue(object : Callback<WatchListRemoveResponse> {
            override fun onResponse(
                call: Call<WatchListRemoveResponse>,
                response: Response<WatchListRemoveResponse>
            ) {
                _response.value = response.code()
            }

            override fun onFailure(call: Call<WatchListRemoveResponse>, t: Throwable) {
                _response.value = 0
            }
        })
        return response
    }


    fun increaseCount(productId: String): LiveData<Int> {
        val _response = MutableLiveData<Int>()
        val response: LiveData<Int> = _response

        val incCountObj = IncReqData(productId)
        val tokenJWT =
            ApplicationClass.sharedPreferences!!.getString("JWTtoken", null).toString()
        val callApi =
            ApplicationApi.retrofitService.incProductCount(incCountObj, "Bearer $tokenJWT")
        callApi.enqueue(object : Callback<IncrementResponseData> {
            override fun onResponse(
                call: Call<IncrementResponseData>,
                response: Response<IncrementResponseData>
            ) {
                _response.value = response.code()
            }

            override fun onFailure(call: Call<IncrementResponseData>, t: Throwable) {
                _response.value = 0
            }
        })
        return response
    }


    fun decreaseCount(productId: String): LiveData<Int> {
        val _response = MutableLiveData<Int>()
        val response: LiveData<Int> = _response

        val decCountObj = IncReqData(productId)
        val tokenJWT =
            ApplicationClass.sharedPreferences!!.getString("JWTtoken", null).toString()
        val callApi =
            ApplicationApi.retrofitService.decProductCount(decCountObj, "Bearer $tokenJWT")
        callApi.enqueue(object : Callback<IncrementResponseData> {
            override fun onResponse(
                call: Call<IncrementResponseData>,
                response: Response<IncrementResponseData>
            ) {
                _response.value = response.code()
            }

            override fun onFailure(call: Call<IncrementResponseData>, t: Throwable) {
                _response.value = 0
            }
        })
        return response
    }


    fun placeOrder(cartTotal: Double, cartId: String): LiveData<Int> {
        Log.e("ERRRROR", "$cartId $cartTotal")
        val _response = MutableLiveData<Int>()
        val response: LiveData<Int> = _response
        val placeOrderobj = PlaceOrderRequestData(cartId, cartTotal.toString())
        val tokenJWT = ApplicationClass.sharedPreferences!!.getString("JWTtoken", null).toString()
        viewModelScope.launch(Dispatchers.IO) {
            val callApi =
                ApplicationApi.retrofitService.placeOrder(placeOrderobj, "Bearer $tokenJWT")
            callApi.enqueue(object : Callback<PlaceOrderResponse> {
                override fun onResponse(
                    call: Call<PlaceOrderResponse>,
                    response: Response<PlaceOrderResponse>
                ) {
                    Log.e("ERRRROR", response.code().toString())
                    _response.value = response.code()
                    cartResponseData()
                }

                override fun onFailure(call: Call<PlaceOrderResponse>, t: Throwable) {
                    _response.value = 0
                }

            })
        }
        return response
    }

    fun loadOrderedProducts() {
        val tokenJWT = ApplicationClass.sharedPreferences!!.getString("JWTtoken", null).toString()
        viewModelScope.launch(Dispatchers.IO) {
            val callApi = ApplicationApi.retrofitService.getOrderList("Bearer $tokenJWT")
            callApi.enqueue(object : Callback<OrderHistoryResponse> {
                override fun onResponse(
                    call: Call<OrderHistoryResponse>,
                    response: Response<OrderHistoryResponse>
                ) {
                    if (response.code() == 200) {

                        _orderedProducts.value = response.body()!!.data
                    }
                }

                override fun onFailure(call: Call<OrderHistoryResponse>, t: Throwable) {
                }

            })
        }
    }

    fun addToFirebaseWishlist(productDetails: ProductDetails, onComplete: (Boolean) -> Unit) {
        val userId = ApplicationClass.sharedPreferences?.getString("userId", "")
        val userDocRef: DocumentReference = db.collection("users").document(userId ?: "User")

        db.runTransaction { transaction ->
            val userDoc = transaction.get(userDocRef)
            if (!userDoc.exists()) {
                val newUserDoc = hashMapOf(
                    "wishlist" to listOf(mapOf("product" to productDetails))
                    // Add other fields as needed
                )
                transaction.set(userDocRef, newUserDoc)
            } else {
                val currentCart = userDoc.get("wishlist") as? List<Map<String, Any>> ?: emptyList()

                // Add the new product to the cart
                val newCart =
                    currentCart + mapOf("product" to productDetails)
                // Update the cart field in the document
                transaction.update(userDocRef, "wishlist", newCart)
            }

            // Transaction success
            true
        }.addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            Log.e("Failure exception", it.message.toString())
            onComplete(false)
        }
    }

    fun removeFromFirebaseWishlist(data: ProductDetails, onComplete: (Boolean) -> Unit) {
        val tempList = mutableListOf<ProductDetails>()
        val userId = ApplicationClass.sharedPreferences?.getString("userId", "")
        val userDocRef: DocumentReference =
            db.collection("users").document(userId ?: "User")
        db.runTransaction { transaction ->
            val userDoc = transaction.get(userDocRef)
            if (userDoc.exists()) {
                userDocRef.get().addOnSuccessListener {
                    try {
                        val result = it.data?.get("wishlist") as List<Map<Any, Any>>

                        result.forEach { map ->
                            val productMap = map["product"] as Map<*, *>
                            val product = ProductDetails(
                                _id = productMap["_id"] as String,
                                description = (productMap["description"] as String),
                                imageUrl = (productMap["imageUrl"] as String),
                                price = (productMap["price"] as String),  // Assuming the price is stored as a double
                                title = productMap["title"] as String,
                                isFirebaseProduct = productMap["isFirebaseProduct"] as Boolean?
                            )
                            Log.e("Hereee data", map["product"].toString())
                            if (data._id == productMap["_id"]) {

                            } else {
                                tempList.add(product)
                            }
                        }
                        userDocRef.update("wishlist", tempList)
                        onComplete(true)
                    } catch (e: Exception) {
                        onComplete(false)
                        Log.e("exc", e.toString())
                    }
                }.addOnFailureListener {
                    onComplete(true)
                    Log.e("exc", it.message.toString())
                }
            }
        }
    }
}


data class FirebaseCartProduct(val count: String, val product: ProductDetails)