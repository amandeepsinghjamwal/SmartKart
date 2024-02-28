package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.myapplication.api.ApplicationApi
import com.example.myapplication.api.models.PlaceOrderRequestData
import com.example.myapplication.api.models.PlaceOrderResponse
import com.example.myapplication.databinding.ActivityPaymentAddressScreenBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPaymentAddressScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentAddressScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val cartTotal = intent.getStringExtra("cart_total")
        val cartId = intent.getStringExtra("cart_id")

        val placeOrderobj = PlaceOrderRequestData(cartId?:"", cartTotal?:"")
        val tokenJWT = ApplicationClass.sharedPreferences!!.getString("JWTtoken", null).toString()
        binding.amountToPay.text = "$ $cartTotal"
       binding.placeOrderButton.setOnClickListener{
           binding.shadow.visibility = View.VISIBLE
           binding.progressCircular.visibility = View.VISIBLE
           placeOrder(placeOrderobj,tokenJWT)
       }
        binding.back.setOnClickListener{
            finish()
        }
        binding.continueShopping.setOnClickListener{
            finish()
        }
    }

    private fun placeOrder(placeOrderObj:PlaceOrderRequestData,tokenJWT:String){
        val callApi =
            ApplicationApi.retrofitService.placeOrder(placeOrderObj, "Bearer $tokenJWT")
        callApi.enqueue(object : Callback<PlaceOrderResponse> {
            override fun onResponse(
                call: Call<PlaceOrderResponse>,
                response: Response<PlaceOrderResponse>
            ) {
                if(response.code()==201){
                    Log.e("ERRRROR", response.code().toString())
                    binding.progressCircular.visibility = View.GONE
                    binding.orderPlacedCard.visibility = View.VISIBLE
                }else{
                    binding.progressCircular.visibility = View.GONE
                    binding.orderPlacedCard.visibility = View.GONE
                    binding.shadow.visibility = View.GONE
                    Snackbar.make(binding.root,"Failed to place order",Snackbar.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<PlaceOrderResponse>, t: Throwable) {
//                _response.value = 0
                binding.progressCircular.visibility = View.GONE
                binding.orderPlacedCard.visibility = View.GONE
                binding.shadow.visibility = View.GONE
                Snackbar.make(binding.root,"Failed to place order",Snackbar.LENGTH_SHORT).show()
            }

        })
    }
}