package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.myapplication.ApplicationClass
import com.example.myapplication.api.ApplicationApi
import com.example.myapplication.api.models.*
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
                            _productList.value = response.body()!!.data
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
                                _cartResponseData.value = response.body()!!.data!!
                                _totalPrice.value = response.body()!!.cartTotal!!
                            } else {
                                _cartResponseData.value = data
                                _totalPrice.value = 0.0
                            }
                        }
                    }
                })
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
}

//class ProductViewModelFactory(
//    private val productDao: ProductDao
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return ProductViewModel(productDao) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}