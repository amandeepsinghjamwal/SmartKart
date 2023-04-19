package com.example.myapplication.entry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.myapplication.ApplicationClass
import com.example.myapplication.api.ApplicationApi
import com.example.myapplication.api.models.RegisterResponseData
import com.example.myapplication.api.models.ResetPasswordData
import com.example.myapplication.databinding.ActivityResetPasswordBinding
import com.example.myapplication.home.HomeScreen
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Pattern

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding:ActivityResetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val jwt=ApplicationClass.sharedPreferences!!.getString("JWTtoken",null).toString()
        binding.submitBtn.setOnClickListener {
            if (checkInvalidConditions(binding.resetPassword.text.toString(), binding.resetcnfrmPassword.text.toString())) {
                changePassword(binding.resetPassword.text.toString(),binding.resetcnfrmPassword.text.toString(),jwt)
            }
        }
    }

    private fun changePassword(password: String, confirmPassword: String,JWT:String) {
        binding.progressBar.visibility= View.VISIBLE
        binding.overlay.visibility= View.VISIBLE
        val passwordObj=ResetPasswordData(password,confirmPassword)
        Log.e("JWT", "$JWT $password $confirmPassword")

        val callApi=ApplicationApi.retrofitService.resetPassword(passwordObj,"Bearer $JWT")

        callApi.enqueue(object : retrofit2.Callback<RegisterResponseData>{
            override fun onResponse(
                call: Call<RegisterResponseData>,
                response: Response<RegisterResponseData>
            ) {
                if(response.code()==200){
                    binding.progressBar.visibility= View.GONE
                    binding.overlay.visibility= View.GONE
                    Toast.makeText(this@ResetPasswordActivity,"Password changed successfully",Toast.LENGTH_SHORT).show()
                    intent=Intent(this@ResetPasswordActivity,HomeScreen::class.java).apply {
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
                else if(response.code()==400){
                    binding.progressBar.visibility= View.GONE
                    binding.overlay.visibility= View.GONE
                    Snackbar.make(binding.snackReset,"Something went wrong",Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponseData>, t: Throwable) {
                binding.progressBar.visibility= View.GONE
                binding.overlay.visibility= View.GONE
                Snackbar.make(binding.snackReset,"Some error occurred",Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkInvalidConditions(password: String, confirmPassword: String): Boolean {
        if(password.isBlank()||confirmPassword.isBlank()){
            if(password.isBlank()){
                binding.resetPassword.error="Please fill this field"
                return false}
            else if(confirmPassword.isBlank()){
                binding.resetcnfrmPassword.error="Please fill this field"
                return false}
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
            binding.resetPassword.error="Password must be greater than 6 characters and must contain one uppercase,one lowercase letter, a number and a special character"
            return false
        }
        else if (password!=confirmPassword){
            binding.resetcnfrmPassword.error="passwords do not match"
            return false
        }
        return true
    }
}

