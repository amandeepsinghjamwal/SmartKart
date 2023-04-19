package com.example.myapplication.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ApplicationClass
import com.example.myapplication.R
import com.example.myapplication.adapter.CartAdapter
import com.example.myapplication.databinding.FragmentCartBinding
import com.example.myapplication.viewmodel.ProductViewModel
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class CartFragment : Fragment() {
    private var _binding:FragmentCartBinding?=null
    private val binding get() = _binding!!
    private val viewModel:ProductViewModel by activityViewModels()
    private lateinit var adapter: CartAdapter
    private lateinit var cartId:String
    private var cartTotal by Delegates.notNull<Double>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentCartBinding.inflate(inflater,container,false)
        val home=true
        adapter = CartAdapter(viewModel,this.viewLifecycleOwner,activity as HomeScreen) {
            (activity as HomeScreen).gotoProductView(
                it.productDetails._id,
                it.productDetails.title,
                it.productDetails.description,
                it.productDetails.imageUrl,
                it.productDetails.price,
                it.productDetails.watchListId,
                it._id,
                home
            )
        }

        binding.placeOrderButton.isEnabled=false
        viewModel.cartResponseData()
        binding.cartRecView.adapter = adapter
        viewModel.cartResponseData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if(it.isEmpty()){
                binding.emptyImg.visibility=View.VISIBLE
                binding.emptyText.visibility=View.VISIBLE
                binding.emptyTextShopping.visibility=View.VISIBLE
            }
            binding.overlay.visibility=View.GONE
            binding.loadinPprogressBar.visibility=View.GONE
        }
        viewModel.cartResponseData.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                cartId= it[0].cartId.toString()
            }
        }
        viewModel.totalPrice.observe(this.viewLifecycleOwner) {
            if(it==0.0){
                binding.totalAmt.text=  getString(R.string.dollar, "0")
                binding.placeOrderButton.isEnabled=false
                binding.progressBar.visibility=View.GONE
            }
            else{
                binding.placeOrderButton.isEnabled=true
                val roundoff = (it * 100.0).roundToInt() / 100.0
                binding.totalAmt.text= getString(R.string.dollar, "$roundoff")
                cartTotal=it
                binding.emptyImg.visibility=View.GONE
                binding.emptyText.visibility=View.GONE
                binding.emptyTextShopping.visibility=View.GONE
            }
        }
        binding.cartRecView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        binding.cartRecView.layoutManager = LinearLayoutManager(this.context)

        binding.placeOrderButton.setOnClickListener{
            binding.placeOrderButton.isEnabled=false
            val data=viewModel.placeOrder(cartTotal,cartId)
            data.observe(viewLifecycleOwner){
                if(it==201){
                    binding.progressBar.visibility=View.GONE
                    if(ApplicationClass.sharedPreferences!!.getBoolean("Notification",false)){
                        (activity as HomeScreen).showNotifications()
                    }
                    (activity as HomeScreen).showSnackBar("Order Placed Successfully")
                }
                else{
                    (activity as HomeScreen).showSnackBar("Something went wrong")
                    binding.placeOrderButton.isEnabled=true
                }
            }
        }
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        binding.overlay.visibility=View.VISIBLE
        binding.loadinPprogressBar.visibility=View.VISIBLE
        viewModel.cartResponseData()

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as HomeScreen).replaceFragmentWithFocus()
                requireActivity().supportFragmentManager.popBackStack()
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}