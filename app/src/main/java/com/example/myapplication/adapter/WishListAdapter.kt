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
import com.example.myapplication.account.WishlistActivity
import com.example.myapplication.api.models.WishlistResponseData
import com.example.myapplication.databinding.WishlistProductVisualBinding
import com.example.myapplication.viewmodel.ProductViewModel


class WishListAdapter(var viewModel: ProductViewModel,var lifecycleOwner: LifecycleOwner,var activity:Activity,private val onItemClicked:(WishlistResponseData)-> Unit):
    ListAdapter<WishlistResponseData,WishListAdapter.ItemViewHolder>(DiffCallBack) {
   inner class ItemViewHolder( var binding: WishlistProductVisualBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")

        fun bind(data: WishlistResponseData){
            binding.apply {
                binding.homeProductName.text=data.productDetails.title
                binding.homeProductPrice.text="$ "+ data.productDetails.price
                try{
                    binding.homeProductImage.load(data.productDetails.imageUrl)
                }
                catch (e:Exception){
                    binding.homeProductImage.load(R.drawable.banner)
                }
                binding.removeFromCart.setOnClickListener{
                    binding.removeFromCart.isEnabled=false
                    (activity as WishlistActivity).showSnackBar("Removing item from wishlist")
                    val dat=viewModel.removeFromWishlist(data._id)
                    dat.observe(lifecycleOwner){
                        if(it==200){
                            viewModel.loadWishlistData()
                            (activity as WishlistActivity).showSnackBar("Item removed from wishlist")
                        }
                        else{
                            (activity as WishlistActivity).showSnackBar("Error removing from wishlist")
                            binding.removeFromCart.isEnabled=true
                        }
                    }
                }
            }
        }
    }

    companion object{
        private val DiffCallBack= object : DiffUtil.ItemCallback<WishlistResponseData>(){
            override fun areItemsTheSame(oldItem: WishlistResponseData, newItem: WishlistResponseData): Boolean {
                return oldItem._id==newItem._id
            }

            override fun areContentsTheSame(oldItem: WishlistResponseData, newItem: WishlistResponseData): Boolean {
                return oldItem==newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        return ItemViewHolder(WishlistProductVisualBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current=getItem(position)
        holder.itemView.setOnClickListener{
            onItemClicked(current)
        }
        holder.bind(current)
    }
}