package com.example.myapplication.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.load
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityOrderDetailsBinding

class OrderDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val productName=intent.getStringExtra("pName")
        val productDesc=intent.getStringExtra("pDesc")
        val productPrice=intent.getStringExtra("pPrice")
        val productQuantity=intent.getStringExtra("pQuantity")
        val productTotal=intent.getStringExtra("pTotal")
        val productImage=intent.getStringExtra("pImg")
        val productOrderDate=intent.getStringExtra("pDate")
        val productOrderId=intent.getStringExtra("pOid")

        binding.back.setOnClickListener{
            finish()
        }
        binding.orderId.text=getString(R.string.order_id,productOrderId)
        binding.orderDate.text=productOrderDate!!.substring(0,10)
        binding.orderTime.text=productOrderDate.substring(11,19)
        binding.homeProductImage.load(productImage)
        binding.homeProductName.text=productName
        binding.orderProductDesc.text=productDesc
        binding.prodPrice.text=getString(R.string.dollar, productPrice)
        binding.prodQuantity.text=productQuantity
        binding.orderProductPrice.text=getString(R.string.dollar, productPrice)
        binding.totalAmt.text=getString(R.string.dollar, productTotal)
        binding.prodName.text=productName
    }
}