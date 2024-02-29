package com.example.myapplication.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.ApplicationClass
import com.example.myapplication.MainActivity
import com.example.myapplication.adapter.ProductListAdapter
import com.example.myapplication.api.models.ProductDetails
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.viewmodel.ProductViewModel

class MainFragment : Fragment() {
    private lateinit var adapter: ProductListAdapter
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.overlay.visibility=View.VISIBLE
        binding.progressBar.visibility=View.VISIBLE
        adapter = ProductListAdapter(requireContext(),viewModel,this.viewLifecycleOwner,activity as HomeScreen) {
            (activity as HomeScreen).gotoProductView(
                it._id,
                it.title,
                it.description,
                it.imageUrl,
                it.price,
                it.watchListId,
                it.cartItemId,
                false
            )
        }
        binding.homeScreenList.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        binding.homeScreenList.layoutManager = GridLayoutManager(this.context,2)
        binding.homeScreenList.adapter = adapter
        viewModel.productList.observe(this.viewLifecycleOwner) { products ->
            products.let {
                adapter.submitList(it)
                binding.overlay.visibility=View.GONE
                binding.progressBar.visibility=View.GONE
            }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(p0: String?): Boolean {
                val list: MutableList<ProductDetails> = emptyList<ProductDetails>().toMutableList()
                viewModel.productList.observe(viewLifecycleOwner){product->
                    product.forEach{
                        if (it.title.lowercase().contains(p0!!.lowercase())){
                            list += it
                        }
                    }
                    adapter.submitList(list)
                }
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val dat=viewModel.loadProductData()
        dat.observe(viewLifecycleOwner){
            if(it==500){
                Toast.makeText(requireContext(),"Session expired, Please login again",Toast.LENGTH_SHORT).show()
                ApplicationClass.editor!!.clear().apply()
                startActivity(Intent(requireContext(),MainActivity::class.java).apply {
                    flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        }
        binding.overlay.visibility=View.VISIBLE
        binding.progressBar.visibility=View.VISIBLE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}