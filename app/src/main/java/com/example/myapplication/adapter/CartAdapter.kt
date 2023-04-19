package com.example.myapplication.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.R
import com.example.myapplication.api.models.CartResponseData
import com.example.myapplication.databinding.CartProductVisualBinding
import com.example.myapplication.home.HomeScreen
import com.example.myapplication.viewmodel.ProductViewModel
import java.util.*
import kotlin.math.roundToInt


class CartAdapter(var viewModel: ProductViewModel, var lifecycleOwner: LifecycleOwner,var activity: Activity, private val onItemClicked:(CartResponseData)-> Unit):
    ListAdapter<CartResponseData,CartAdapter.ItemViewHolder>(DiffCallBack) {
    inner class ItemViewHolder(var binding: CartProductVisualBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: CartResponseData){
            binding.apply {
                binding.homeProductName.text=data.productDetails.title
                binding.homeProductPrice.text="$ " + data.itemTotal.toString()
                try{
                    binding.homeProductImage.load(data.productDetails.imageUrl)
                }
                catch (e:Exception){
                    binding.homeProductImage.load(R.drawable.banner)
                }

                binding.removeFromCart.setOnClickListener {

                    (activity as HomeScreen).showAlert("Remove from cart","Do you really want to remove this product?"){
                        if(it){
                            (activity as HomeScreen).showProgressBar()
                            val responseCode = viewModel.removeFromCart(data._id!!.toString())
                            responseCode.observe(lifecycleOwner) {
                                when (responseCode.value) {
                                    200 -> {
                                        (activity as HomeScreen).hideProgressBar()
                                        (activity as HomeScreen).showSnackBar("${data.productDetails.title} is removed from cart")

                                    }
                                    400 -> {
                                        (activity as HomeScreen).hideProgressBar()
                                        (activity as HomeScreen).showSnackBar("Failed to remove from cart")
                                    }
                                    else -> {
                                        (activity as HomeScreen).hideProgressBar()
                                        (activity as HomeScreen).showSnackBar("Some error occurred")
                                    }
                                }
                            }
                        }
                    }
                }

                if (data.quantity != null) {
                    binding.productCount.text=data.quantity.toString()
                }
                val roundoff = (data.itemTotal!! * 100.0).roundToInt() / 100.0
                binding.homeProductPrice.text="$ $roundoff"
                binding.incButton.setOnClickListener{
                    (activity as HomeScreen).showProgressBar()
                    binding.incButton.isEnabled=false
                    val responseCode=viewModel.increaseCount(data._id!!)
                    responseCode.observe(lifecycleOwner){
                        viewModel.cartResponseData()
                        if(responseCode.value!=200){
                            (activity as HomeScreen).hideProgressBar()
                            binding.incButton.isEnabled=true
                            (activity as HomeScreen).showSnackBar("Something Went wrong")
                        }
                        else{
                            val roundOff = (data.itemTotal * 100.0).roundToInt() / 100.0
                            binding.incButton.isEnabled=true
                            binding.productCount.text=(data.quantity?.plus(1)).toString()
                            binding.productCount.text=data.quantity.toString()
                            binding.homeProductPrice.text="$ $roundOff"
                            (activity as HomeScreen).hideProgressBar()
                        }
                    }
                }

                binding.decButton.setOnClickListener{
                    (activity as HomeScreen).showProgressBar()
                    binding.decButton.isEnabled=false
                    val responseCode=viewModel.decreaseCount(data._id!!)
                    responseCode.observe(lifecycleOwner){
                        if(responseCode.value!=200){
                            binding.decButton.isEnabled=true
                            (activity as HomeScreen).showProgressBar()
                            (activity as HomeScreen).showSnackBar("Something Went wrong")
                        }
                        else{
                            binding.decButton.isEnabled=true
                            (activity as HomeScreen).hideProgressBar()
                            viewModel.cartResponseData()
                            binding.productCount.text=data.quantity.toString()
                            val roundOff = (data.itemTotal * 100.0).roundToInt() / 100.0
                            binding.homeProductPrice.text="$ $roundOff"
                        }
                    }
                }
            }
        }
    }

    companion object{
        private val DiffCallBack= object : DiffUtil.ItemCallback<CartResponseData>(){
            override fun areItemsTheSame(oldItem: CartResponseData, newItem: CartResponseData): Boolean {
                return oldItem._id==newItem._id
            }

            override fun areContentsTheSame(oldItem: CartResponseData, newItem: CartResponseData): Boolean {
                return oldItem==newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(CartProductVisualBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current=getItem(position)
        holder.itemView.setOnClickListener{
            onItemClicked(current)
        }
        holder.bind(current)
    }
}