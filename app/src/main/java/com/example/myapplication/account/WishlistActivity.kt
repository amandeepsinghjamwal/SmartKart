package com.example.myapplication.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.myapplication.adapter.WishListAdapter
import com.example.myapplication.databinding.ActivityWishlistBinding
import com.example.myapplication.viewmodel.ProductViewModel
import com.google.android.material.snackbar.Snackbar

class WishlistActivity : AppCompatActivity(){
    private lateinit var viewModel:ProductViewModel
    private lateinit var adapter: WishListAdapter
    private lateinit var binding: ActivityWishlistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        adapter = WishListAdapter(viewModel,this,this@WishlistActivity) {
        }
        binding.wishListScreenList.adapter = adapter
        binding.wishListScreenList.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        binding.wishListScreenList.layoutManager = LinearLayoutManager(this)
        viewModel.wishlistProducts.observe(this) { products ->
            products.let {
                if(it.isEmpty()){
                    binding.emptyImg.visibility=View.VISIBLE
                    binding.emptyText.visibility=View.VISIBLE
                }
                else{
                    binding.emptyImg.visibility=View.GONE
                    binding.emptyText.visibility=View.GONE
                }
                adapter.submitList(it)
                binding.overlay.visibility=View.GONE
                binding.progressBar.visibility=View.GONE
            }
        }
        binding.back.setOnClickListener{
            finish()
        }
    }


    override fun onResume() {
        super.onResume()
        binding.overlay.visibility=View.VISIBLE
        binding.progressBar.visibility= View.VISIBLE
        viewModel.loadWishlistData()
    }

    fun showSnackBar(msg:String){
        Snackbar.make(binding.snackbarView,msg,Snackbar.LENGTH_SHORT).show()
    }

}
