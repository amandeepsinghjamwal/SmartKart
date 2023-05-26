package com.example.myapplication.entry


import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.MainActivity
import com.example.myapplication.api.ApplicationApi
import com.example.myapplication.api.models.RegisterResponseData
import com.example.myapplication.api.models.RegisterUserData
import com.example.myapplication.databinding.FragmentSignupBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Pattern


class SignupFragment : Fragment() {
    private var _binding:FragmentSignupBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentSignupBinding.inflate(inflater,container,false)
        binding.signupBtn.isEnabled=true
        binding.signupBtn.setOnClickListener{
            if(checkInvalidConditions(binding.signupName.text.toString(),binding.signupNumber.text.toString(),binding.signUpEmail.text.toString(),binding.signupPassword.text.toString(),binding.signupCnfrmPassword.text.toString())){
                binding.signupBtn.isEnabled=false
                register(binding.signUpEmail.text.toString(),binding.signupNumber.text.toString(),binding.signupName.text.toString(),binding.signupPassword.text.toString())
            }
        }
        return binding.root
    }


    private fun checkInvalidConditions(name:String, number: String, email:String, password:String, confirmPassword:String):Boolean {
        if(email.isBlank()||password.isBlank()||number.isBlank()||password.isBlank()||confirmPassword.isBlank()){

             if(name.isBlank()){
                binding.signupName.error="Please fill this field"
            return false}
             else if(number.isBlank()){
                 binding.signupNumber.error="Please fill this field"
                 return false
             }
             else if(email.isBlank()){
                 binding.signUpEmail.error="Please fill this field"
                 return false
             }
             else if(password.isBlank()){
                 binding.signupPassword.error="Please fill this field"
                 return false
             }
             else if(confirmPassword.isBlank()){
                 binding.signupCnfrmPassword.error="Please fill this field"
                 return false
             }
        }
        if(name.length>20){
                binding.signupName.error="Name cannot be more than 20 characters"
                return false
        }
        if(number.length>10){
                binding.signupNumber.error="Enter valid number"
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.signUpEmail.error="Please enter valid email"
            return false
        }
        val passwordRegex = Pattern.compile("^" +
                "(?=.*\\d)" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{6,}" +               //at least 6 characters
                "$")

        if(!passwordRegex.matcher(password).matches()){
            binding.signupPassword.error="Password must be greater than 6 characters and must contain one uppercase,one lowercase letter, a number and a special character"
            return false
        }
        else if (password!=confirmPassword){
                binding.signupCnfrmPassword.error="passwords do not match"
            return false
            }
        return true
    }

    private fun register(email:String, number: String, name: String, password: String){
            binding.progressBar.visibility=View.VISIBLE

            val registerUserDataObject= RegisterUserData(email, number, name, password)
            val callApi=ApplicationApi.retrofitService.registerUser(registerUserDataObject)
            callApi.enqueue(object: retrofit2.Callback<RegisterResponseData>{
                override fun onResponse(
                    call: Call<RegisterResponseData>,
                    response: Response<RegisterResponseData>
                ) {

                        if(response.code()==201){
                            binding.signupBtn.isEnabled=true
                            Snackbar.make(binding.signupSnack,"Please verify your email to proceed",Snackbar.LENGTH_SHORT).show()
                            if(response.body()!!.status==1){
                                binding.progressBar.visibility=View.GONE
                                (activity as MainActivity).gotoOtpScreen(response.body()!!.data!!._id,false,response.body()!!.data!!.emailId)
                            }
                        }
                    else if(response.code()==400){
                            binding.signupBtn.isEnabled=true
                            binding.progressBar.visibility=View.GONE
                            binding.signUpEmail.error="User Exists. Try logging in"
                        }
                }
                override fun onFailure(call: Call<RegisterResponseData>, t: Throwable) {
                    binding.signupBtn.isEnabled=true
                    binding.progressBar.visibility=View.GONE
                    Snackbar.make(binding.signupSnack,"Unsuccessful",Snackbar.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}