package com.example.myapplication.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.OrderAdapter
import com.example.myapplication.databinding.ActivityMyOrdersBinding
import com.example.myapplication.viewmodel.ProductViewModel

class MyOrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyOrdersBinding
    private lateinit var adapter:OrderAdapter
    private lateinit var viewModel: ProductViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        binding.overlay.visibility=View.VISIBLE
        binding.progressBar.visibility=View.VISIBLE
        viewModel.loadOrderedProducts()
        adapter = OrderAdapter {
            gotoOrderDetails(
                it.title,
                it.description,
                it.imageUrl,
                it.price,
                it.orderId,
                it.createdAt,
                it.productTotalAmount,
                it.quantity
            )
        }
        viewModel.orderedProducts.observe(this){products ->
            products.let {
                if(it.isEmpty()){
                    binding.emptyOrderList.visibility=View.VISIBLE
                }
                adapter.submitList(it)
                binding.overlay.visibility=View.GONE
                binding.progressBar.visibility=View.GONE
            }

        }
        binding.back.setOnClickListener{
            finish()
        }
        binding.orderList.adapter= adapter
        binding.orderList.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        binding.orderList.layoutManager = LinearLayoutManager(this)
    }

    private fun gotoOrderDetails(
        title: String,
        description: String,
        imageUrl: String,
        price: String,
        orderId: String,
        createdAt: String,
        productTotalAmount: String,
        quantity: Int
    ) {
        intent= Intent(this,OrderDetailsActivity::class.java).apply {
            putExtra("pName",title)
            putExtra("pDesc",description)
            putExtra("pImg",imageUrl)
            putExtra("pPrice",price)
            putExtra("pOid",orderId)
            putExtra("pDate",createdAt)
            putExtra("pTotal",productTotalAmount)
            putExtra("pQuantity",quantity.toString())
        }
        startActivity(intent)
    }

}