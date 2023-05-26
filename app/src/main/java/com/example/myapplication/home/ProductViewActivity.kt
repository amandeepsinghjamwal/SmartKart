package com.example.myapplication.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.myapplication.ApplicationClass
import com.example.myapplication.R
import com.example.myapplication.api.ApplicationApi
import com.example.myapplication.api.models.*
import com.example.myapplication.databinding.ActivityProductViewBinding
import com.example.myapplication.viewmodel.ProductViewModel
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Response

class ProductViewActivity : AppCompatActivity() {
    private var isWishlisted: Boolean = true
    private var wishlistProductId: String? = null
    lateinit var viewModel: ProductViewModel
    lateinit var binding: ActivityProductViewBinding
    private var cartItemId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        binding.back.setOnClickListener {
            viewModel.loadWishlistData()
            finish()
        }

        val pImage = intent.getStringExtra("pImgUrl")
        val pName = intent.getStringExtra("pName")
        val pDesc = intent.getStringExtra("pDesc")
        val pID = intent.getStringExtra("PID")
        val price = intent.getStringExtra("price")
        val home = intent.getBooleanExtra("home", false)
        if (home) {
            binding.addToWishlist.visibility = View.GONE
        }
        wishlistProductId = intent.getStringExtra("Wid")
        isWishlisted = intent.getBooleanExtra("wishlisted", false)
        cartItemId = intent.getStringExtra("cId")
        Log.e("Errror", wishlistProductId.toString())

        binding.productImage.load(pImage)
        binding.productName.text = pName
        binding.productDescription.text = pDesc
        binding.price.text = getString(R.string.dollar, price)
        if (wishlistProductId != null) {
            binding.addToWishlist.setImageResource(R.drawable.wishlisted)
        }
        binding.addToWishlist.setOnClickListener {
            Snackbar.make(binding.snackbarView, "Removing from wishlist...", Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.addToCartClick).show()
            if (wishlistProductId != null) {
                removeFromWishlist(wishlistProductId.toString())
            } else {
                Snackbar.make(binding.snackbarView, "Adding to wishlist...", Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.addToCartClick).show()
                addToWishlist(pID)
            }
        }
        if (cartItemId != null) {
            binding.addToCartClick.text = getString(R.string.goToCart)
        }
        binding.addToCartClick.setOnClickListener {
            binding.addToCartClick.isEnabled = false
            if (binding.addToCartClick.text == "Go to cart") {
                intent = Intent(this@ProductViewActivity, HomeScreen::class.java).apply {
                    putExtra("gotoCart", true)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            } else {
                addProductToCart(pID!!)
            }
        }
    }

    private fun removeFromWishlist(wishlistProductId2: String) {
        val dat = viewModel.removeFromWishlist(wishlistProductId2)
        dat.observe(this) {
            if (it == 200) {
                wishlistProductId = null
                binding.addToWishlist.setImageResource(R.drawable.wishlist_stroked)
                Snackbar.make(
                    binding.snackbarView,
                    "Item removed from wishlist",
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(binding.addToCartClick).show()
            } else {
                Snackbar.make(
                    binding.snackbarView,
                    "Failed to remove item from wishlist",
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(binding.addToCartClick).show()
            }
        }
    }

    private fun addToWishlist(pid: String?) {

        val data = viewModel.addToWishlist(pid)
        data.observe(this) {
            if (it != "fail") {
                wishlistProductId = it
                binding.addToWishlist.setImageResource(R.drawable.wishlisted)
                Snackbar.make(binding.snackbarView, "Item added to wishlist", Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.addToCartClick).show()
            } else {
                Snackbar.make(
                    binding.snackbarView,
                    "Failed to add item to wishlist",
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(binding.addToCartClick).show()
            }
        }
    }

    private fun addProductToCart(pid: String) {

        val addToCartObj = WatchlistRequestData(pid)
        val tokenJWT = ApplicationClass.sharedPreferences!!.getString("JWTtoken", null).toString()
        val callApi = ApplicationApi.retrofitService.addToCart(addToCartObj, "Bearer $tokenJWT")
        callApi.enqueue(object : retrofit2.Callback<AddtoCartResponse> {
            override fun onResponse(
                call: Call<AddtoCartResponse>,
                response: Response<AddtoCartResponse>
            ) {
                if (response.code() == 201) {
                    binding.addToCartClick.text = getString(R.string.goToCart)
                    viewModel.loadProductData()
                    binding.addToCartClick.isEnabled = true
                    Snackbar.make(
                        binding.snackbarView,
                        "Added to cart successfully!",
                        Snackbar.LENGTH_SHORT
                    ).setAnchorView(binding.addToCartClick).show()
                } else if (response.code() == 400) {
                    Snackbar.make(
                        binding.snackbarView,
                        response.body()!!.msg.toString(),
                        Snackbar.LENGTH_SHORT
                    ).setAnchorView(binding.addToCartClick).show()
                    binding.addToCartClick.isEnabled = true
                }
            }

            override fun onFailure(call: Call<AddtoCartResponse>, t: Throwable) {
                binding.addToCartClick.isEnabled = true
                Snackbar.make(binding.snackbarView, "Some error occurred", Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.addToCartClick).show()
            }
        })
    }
}