package com.example.myapplication.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.R
import com.example.myapplication.api.models.ProductDetails
import com.example.myapplication.databinding.HomeProductVisualBinding
import com.example.myapplication.home.HomeScreen
import com.example.myapplication.viewmodel.ProductViewModel


class ProductListAdapter(var context: Context,var viewModel: ProductViewModel, var lifecycleOwner: LifecycleOwner, var activity: Activity, private val onItemClicked:(ProductDetails)-> Unit):
    ListAdapter<ProductDetails,ProductListAdapter.ItemViewHolder>(DiffCallBack) {

    inner class ItemViewHolder(var binding: HomeProductVisualBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: ProductDetails){
            binding.apply {

//                Adding to wishlist from product list

                if(data.isWishlisted==true){
                    binding.addToWishlist.setImageResource(R.drawable.wishlisted)
                }
                if(data.isWishlisted==false){
                    binding.addToWishlist.setImageResource(R.drawable.wishlist_stroked)
                }

                    binding.addToWishlist.setOnClickListener{
                        (activity as HomeScreen).showSnackBar("Adding to wishlist...")
                        binding.addToWishlist.isClickable=false
                        if(data.isWishlisted==false){
                            val dat= viewModel.addToWishlist(data._id)
                            dat.observe(lifecycleOwner){
                                if(it!="fail"){
                                    binding.addToWishlist.isClickable=true
                                    (activity as HomeScreen).showSnackBar("${data.title} is added to wishlist")
                                    binding.addToWishlist.setImageResource(R.drawable.wishlisted)
                                    data.isWishlisted=true
                                    data.watchListId=it
                                }
                                else{
                                    binding.addToWishlist.isClickable=true
                                    (activity as HomeScreen).showSnackBar("Error adding to wishlist")
                                }
                            }
                        }
                        else {
                            (activity as HomeScreen).showSnackBar("Removing from wishlist...")
                            val dat= viewModel.removeFromWishlist(data.watchListId)
                            dat.observe(lifecycleOwner){
                                    if(it==200){
                                        binding.addToWishlist.setImageResource(R.drawable.wishlist_stroked)
                                        (activity as HomeScreen).showSnackBar("${data.title} is removed from wishlist")
                                        data.isWishlisted=false
                                        data.watchListId=null
                                        binding.addToWishlist.isClickable=true
                                    }
                                    else{
                                        (activity as HomeScreen).showSnackBar("Error removing from wishlist")
                                        binding.addToWishlist.isClickable=true
                                    }
                                }
                            }
                        }
                    }


            //add to cart
                if(data.isInCart==true){
                    binding.addToCart.text="Go to cart"
                    binding.addToCart.setBackgroundColor(context.getColor(R.color.white))
                    binding.addToCart.strokeWidth=1
                    binding.addToCart.setTextColor(context.getColor(R.color.teal_700))
                }
            else{
                    binding.addToCart.text="Add to cart"
                    binding.addToCart.setBackgroundColor(context.getColor(R.color.teal_700))
                    binding.addToCart.strokeWidth=0
                    binding.addToCart.setTextColor(context.getColor(R.color.white))
                }
                binding.addToCart.setOnClickListener{
                    binding.addToCart.isEnabled=false
                    if(data.isInCart==false){
                        binding.addToCart.text="Adding.."
                          val dat=viewModel.addProductToCart(data._id)
                         dat.observe(lifecycleOwner){
                             if(it!="fail"){
                                 (activity as HomeScreen).showSnackBar("${data.title} has been added to your cart")
                                 binding.addToCart.setBackgroundColor(context.getColor(R.color.white))
                                 binding.addToCart.strokeWidth=1
                                 binding.addToCart.setTextColor(context.getColor(R.color.teal_700))
                                 binding.addToCart.isEnabled=true
                                 binding.addToCart.text="Go to cart"
                                 data.isInCart=true
                                 
                             }
                             else{
                                 binding.addToCart.isEnabled=true
                                 (activity as HomeScreen).showSnackBar("Failed to add product to cart")
                             }
                         }
                    }
                    else{
                        binding.addToCart.isEnabled=true
                        (activity as HomeScreen).replaceFragmentWithFocusOnCart()
                    }
                }

                binding.homeProductName.text=data.title
                binding.homeProductPrice.text= "$ " + data.price

                try{
                    binding.homeProductImage.load(data.imageUrl)
                }
                catch (e:Exception){
                    binding.homeProductImage.load(R.drawable.banner)
                }
            }
        }


    companion object{
        private val DiffCallBack= object : DiffUtil.ItemCallback<ProductDetails>(){
            override fun areItemsTheSame(oldItem: ProductDetails, newItem: ProductDetails): Boolean {
                return oldItem._id==newItem._id
            }

            override fun areContentsTheSame(oldItem: ProductDetails, newItem: ProductDetails): Boolean {
                return oldItem==newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        return ItemViewHolder(HomeProductVisualBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current=getItem(position)
        holder.itemView.setOnClickListener{
            onItemClicked(current)
        }
        holder.bind(current)
    }
}