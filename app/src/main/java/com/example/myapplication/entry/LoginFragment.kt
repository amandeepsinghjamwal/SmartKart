package com.example.myapplication.entry

import android.content.res.Configuration
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.myapplication.ApplicationClass
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.api.ApplicationApi
import com.example.myapplication.api.models.LoginUserData
import com.example.myapplication.api.models.RegisterResponseData
import com.example.myapplication.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Response


class LoginFragment : Fragment() {

    private var _binding:FragmentLoginBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentLoginBinding.inflate(inflater,container,false)
        binding.loginBtn.setOnClickListener{
            if(checkInvalidConditions(binding.emailFieldLogin.text.toString(),binding.passwordFieldLogin.text.toString())){
                binding.loginBtn.isEnabled=false
                login(binding.emailFieldLogin.text.toString(),binding.passwordFieldLogin.text.toString())
            }
        }

        binding.forgotPassowrd.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }
        binding.toSignUp.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        return binding.root
    }

    private fun login(email: String, password: String) {

        val loginObj=LoginUserData(email,password)
        val callApi=ApplicationApi.retrofitService.userLogin(loginObj)

        callApi.enqueue(object : retrofit2.Callback<RegisterResponseData>{
            override fun onResponse(
                call: Call<RegisterResponseData>,
                response: Response<RegisterResponseData>
            ) {
                if(response.code()==200){

                    ApplicationClass.editor!!.apply {
                        putString("JWTtoken",response.body()!!.data!!.jwtToken)
                        putString("Email",response.body()!!.data!!.emailId)
                        putString("MobileNo",response.body()!!.data!!.mobileNo)
                        putString("userId",response.body()!!.data!!._id)
                        putString("Name", response.body()!!.data!!.name)
                        putString("password",password)
                        putBoolean("Notification",true)
                    }.commit()
                    (activity as MainActivity).changeScreen(response.body()!!.data!!.jwtToken!!)

                }
                else if(response.code()==400){
                    binding.loginBtn.isEnabled=true
                    Snackbar.make(binding.loginFrag,"Invalid Credentials",Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponseData>, t: Throwable) {
                binding.loginBtn.isEnabled=true
                Snackbar.make(binding.loginFrag,"Some error occurred",Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkInvalidConditions(email:String, password:String):Boolean {
        if(email.isBlank()||password.isBlank()){
            if(email.isBlank()){
                binding.emailFieldLogin.error="Please fill this field"
                return false
            }

            else if(password.isBlank()){
            binding.passwordFieldLogin.error="Please fill this field"
            return false}


        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailFieldLogin.error="Enter a valid email id"
            return false
        }
        return true
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding.buttonProgressBar.visibility=View.GONE
        _binding = null
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == Configuration.KEYBOARDHIDDEN_NO){
            binding.logo.visibility=View.GONE
        }
        else if(newConfig.orientation == Configuration.KEYBOARDHIDDEN_YES){
            binding.logo.visibility=View.VISIBLE
        }
    }
}