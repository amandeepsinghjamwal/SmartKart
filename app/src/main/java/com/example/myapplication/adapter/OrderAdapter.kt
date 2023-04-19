package com.example.myapplication.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.R
import com.example.myapplication.api.models.OrderHistoryProductsData
import com.example.myapplication.databinding.OrderProductVisualBinding


class OrderAdapter( private val onItemClicked:(OrderHistoryProductsData)-> Unit):
    ListAdapter<OrderHistoryProductsData,OrderAdapter.ItemViewHolder>(DiffCallBack) {
    class ItemViewHolder(var binding: OrderProductVisualBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: OrderHistoryProductsData){
            binding.apply {
                if(data.title.length>30){
                    binding.homeProductName.text=data.title.substring(0,27)+"..."
                }
                else{
                    binding.homeProductName.text=data.title
                }
                val date= data.createdAt.substring(0,10)
                binding.orderDate.text="Order date: $date"
                try{
                    binding.homeProductImage.load(data.imageUrl)
                }
                catch (e:Exception){
                    binding.homeProductImage.load(R.drawable.banner)
                }
            }
        }
    }

    companion object{
        private val DiffCallBack= object : DiffUtil.ItemCallback<OrderHistoryProductsData>(){
            override fun areItemsTheSame(oldItem: OrderHistoryProductsData, newItem: OrderHistoryProductsData): Boolean {
                return oldItem._id==newItem._id
            }

            override fun areContentsTheSame(oldItem: OrderHistoryProductsData, newItem: OrderHistoryProductsData): Boolean {
                return oldItem==newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        return ItemViewHolder(OrderProductVisualBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current=getItem(position)
        holder.itemView.setOnClickListener{
            onItemClicked(current)
        }
        holder.bind(current)
    }
}