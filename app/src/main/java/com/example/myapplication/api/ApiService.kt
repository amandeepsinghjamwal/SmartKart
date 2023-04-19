package com.example.myapplication.api

import com.example.myapplication.api.models.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

private const val BASE_URL = "https://shopping-app-backend-t4ay.onrender.com/"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    //register
    @POST("user/registerUser")
    fun registerUser(@Body registerUserData: RegisterUserData): Call<RegisterResponseData>

    @POST("user/verifyOtpOnRegister")
    fun verifyOtp(@Body otpRequestData: OtpRequestData): Call<RegisterResponseData>

    @POST("user/verifyOtpOnForgotPassword")
    fun verifyOtpOnForgot(@Body otpRequestData: OtpRequestData):Call<RegisterResponseData>

    @POST("/user/login")
    fun userLogin(@Body loginUser: LoginUserData):Call<RegisterResponseData>

    @POST("/user/resendOtp")
    fun resendOtp(@Body otpRequestData: OtpRequestData):Call<RegisterResponseData>

    @POST("/user/forgotPassword")
    fun forgotPassword(@Body loginUser: LoginUserData):Call<RegisterResponseData>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/user/changePassword")
    fun resetPassword(@Body resetPasswordData: ResetPasswordData,@Header("Authorization") jwtToken:String):Call<RegisterResponseData>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/product/getAllProduct")
    fun getAllProducts(@Header("Authorization") jwtToken:String):Call<ProductResponseData>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/cart/addToCart")
    fun addToCart(@Body productId:WatchlistRequestData ,@Header("Authorization") jwtToken:String):Call<AddtoCartResponse>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/cart/removeProductFromCart")
    fun removeFromCart(@Body removeCartProductId:RemoveFromCartRequestData, @Header("Authorization") jwtToken:String):Call<WatchListRemoveResponse>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/cart/getMyCart")
    fun getCartProducts(@Header("Authorization") jwtToken:String):Call<GetCartResponse>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/watchList/addToWatchList")
    fun addToWishList(@Body productId: WatchlistRequestData , @Header("Authorization") jwtToken:String): Call<AddToWishlistResponse>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/watchList/getWatchList")
    fun getWishlist(@Header("Authorization") jwtToken:String):Call<WishlistResponse>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/watchList/removeFromWatchList")
    fun removeFromWatchlist(@Body watchlistId:WatchListRemoveRequestData, @Header("Authorization") jwtToken:String):Call<WatchListRemoveResponse>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/cart/increaseProductQuantity")
    fun incProductCount(@Body cartProductId:IncReqData, @Header("Authorization") jwtToken:String):Call<IncrementResponseData>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/cart/decreaseProductQuantity")
    fun decProductCount(@Body cartProductId:IncReqData, @Header("Authorization") jwtToken:String):Call<IncrementResponseData>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/order/placeOrder")
    fun placeOrder(@Body cartProductId:PlaceOrderRequestData, @Header("Authorization") jwtToken:String):Call<PlaceOrderResponse>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/order/getOrderHistory")
    fun getOrderList(@Header("Authorization") jwtToken:String):Call<OrderHistoryResponse>

}

object ApplicationApi{
    val retrofitService: ApiService by lazy { retrofit.create(ApiService::class.java) }
}