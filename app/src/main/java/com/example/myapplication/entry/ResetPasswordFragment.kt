package com.example.myapplication.entry

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.MainActivity
import com.example.myapplication.api.ApplicationApi
import com.example.myapplication.api.models.LoginUserData
import com.example.myapplication.api.models.RegisterResponseData
import com.example.myapplication.databinding.FragmentResetPasswordBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Response


class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentResetPasswordBinding.inflate(inflater,container,false)
        binding.submitBtn.setOnClickListener{
            binding.logo.visibility=View.VISIBLE
            if(checkInvalidConditions(binding.resetEmail.text.toString())){
                isRegistered(binding.resetEmail.text.toString())
            }
        }

        return binding.root
    }

    private fun isRegistered(email: String) {
        binding.progressBar.visibility=View.VISIBLE
        binding.overlay.visibility=View.VISIBLE
        val emailObject=LoginUserData(email,null)
        val callApi= ApplicationApi.retrofitService.forgotPassword(emailObject)
        callApi.enqueue(object : retrofit2.Callback<RegisterResponseData>{
            override fun onResponse(
                call: Call<RegisterResponseData>,
                response: Response<RegisterResponseData>
            ) {
                if(response.code()==200){
                    binding.progressBar.visibility=View.GONE
                    binding.overlay.visibility=View.GONE
                    (activity as MainActivity).gotoOtpScreen(response.body()!!.data!!._id,true,response.body()!!.data!!.emailId)
                }
                else if(response.code()==400){
                    binding.progressBar.visibility=View.GONE
                    binding.overlay.visibility=View.GONE
                    Snackbar.make(binding.snackView,"User not registered",Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponseData>, t: Throwable) {
                binding.progressBar.visibility=View.GONE
                binding.overlay.visibility=View.GONE
                Snackbar.make(binding.snackView,"Some error occurred",Snackbar.LENGTH_SHORT).show()
            }
        }
        )
    }

    private fun checkInvalidConditions(email: String): Boolean {
        if(email.isBlank()){
                binding.resetEmail.error="Please fill this field"
                return false
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.resetEmail.error="Please enter valid email"
            return false
        }

        return true
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}