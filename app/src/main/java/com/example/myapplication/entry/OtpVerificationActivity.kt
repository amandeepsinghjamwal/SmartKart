package com.example.myapplication.entry

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.api.ApplicationApi
import com.example.myapplication.api.models.OtpRequestData
import com.example.myapplication.api.models.RegisterResponseData
import com.example.myapplication.databinding.ActivityOtpVerificationBinding
import com.google.android.material.snackbar.Snackbar
import `in`.aabhasjindal.otptextview.OTPListener
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response

class OtpVerificationActivity : AppCompatActivity() {
    lateinit var binding: ActivityOtpVerificationBinding
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId=intent.getStringExtra("userId")
        val forgot=intent.getBooleanExtra("forgot",false)
        binding.emailHere.text= intent.getStringExtra("email")
        binding.otpVieww.otpListener= object : OTPListener {
            override fun onInteractionListener() {
                // fired when user types something in the
            }
            override fun onOTPComplete(otp: String) {
                // fired when user has entered the OTP fully.
                if(forgot){
                    verifyOtpOnForgot(userId!!,otp)
                }
                else{
                    verifyOtp(userId!!,otp)
                }
            }
        }
        binding.resendOtpButton.setOnClickListener{
            if(binding.resendOtpButton.currentTextColor!=Color.GRAY){
                resendOtp(userId)
                binding.resendOtpButton.text=getString(R.string.otp_sent)
            }
            binding.resendOtpButton.setTextColor(Color.GRAY)
            GlobalScope.launch(Dispatchers.Default ) {
                delay(15000)
                binding.resendOtpButton.text=getString(R.string.resendOtp)
                binding.resendOtpButton.setTextColor(Color.parseColor("#FF018786"))
            }
        }
    }

    private fun resendOtp(userId: String?) {
        val otpRequestData=OtpRequestData(userId!!,null)
        val callApi=ApplicationApi.retrofitService.resendOtp(otpRequestData)

        callApi.enqueue(object : retrofit2.Callback<RegisterResponseData>{
            override fun onResponse(
                call: Call<RegisterResponseData>,
                response: Response<RegisterResponseData>
            ) {
                if (response.code()==200){
                    Snackbar.make(binding.otpScreenView,"Otp sent successfully",Snackbar.LENGTH_LONG).show()
                }
                else{
                    Snackbar.make(binding.otpScreenView,"Something went wrong",Snackbar.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<RegisterResponseData>, t: Throwable) {
                Snackbar.make(binding.otpScreenView,"Something went wrong",Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun verifyOtpOnForgot(userId:String, otp:String){
        binding.progressBar.visibility= View.VISIBLE
        binding.overlay.visibility=View.VISIBLE
        val otpRequestObject= OtpRequestData(userId,otp)
        val callApi=ApplicationApi.retrofitService.verifyOtpOnForgot(otpRequestObject)

        callApi.enqueue(object : retrofit2.Callback<RegisterResponseData>{
            override fun onResponse(
                call: Call<RegisterResponseData>,
                response: Response<RegisterResponseData>
            ) {
                if(response.code()==200) {
                    binding.progressBar.visibility = View.GONE
                    binding.overlay.visibility=View.GONE
                    Toast.makeText(this@OtpVerificationActivity,"New password had been sent to mail",Toast.LENGTH_SHORT).show()
                        val intent =
                            (Intent(
                                this@OtpVerificationActivity,
                                MainActivity::class.java
                            )).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        startActivity(intent)
                }
                else if (response.code()==400){
                    binding.progressBar.visibility=View.GONE
                    binding.overlay.visibility=View.GONE
                    Snackbar.make(binding.otpScreenView,"Wrong Otp",Snackbar.LENGTH_LONG).show()
                    binding.otpVieww.showError()
                }
            }
            override fun onFailure(call: Call<RegisterResponseData>, t: Throwable) {
                binding.progressBar.visibility=View.GONE
                binding.overlay.visibility=View.GONE
                Snackbar.make(binding.otpScreenView,"Some error occurred",Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun verifyOtp(userId: String, otp: String) {
        binding.progressBar.visibility= View.VISIBLE
        binding.overlay.visibility=View.VISIBLE
            val otpRequestObject= OtpRequestData(userId,otp)
            val callApi=ApplicationApi.retrofitService.verifyOtp(otpRequestObject)

            callApi.enqueue(object : retrofit2.Callback<RegisterResponseData>{
                override fun onResponse(
                    call: Call<RegisterResponseData>,
                    response: Response<RegisterResponseData>
                ) {
                    if(response.code()==200) {
                        binding.progressBar.visibility = View.GONE
                        binding.overlay.visibility=View.GONE
                        Toast.makeText(this@OtpVerificationActivity,"Registration successful!",Toast.LENGTH_SHORT).show()
                        val intent =
                                (Intent(
                                    this@OtpVerificationActivity,
                                    MainActivity::class.java
                                )).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            startActivity(intent)
                    }
                        else{
                            binding.progressBar.visibility=View.GONE
                        binding.overlay.visibility=View.GONE
                        Snackbar.make(binding.otpScreenView,"Wrong Otp",Snackbar.LENGTH_LONG).show()
                        binding.otpVieww.showError()
                        }
                    }
                override fun onFailure(call: Call<RegisterResponseData>, t: Throwable) {
                    binding.progressBar.visibility=View.GONE
                    binding.overlay.visibility=View.GONE
                    Snackbar.make(binding.otpScreenView,"Some error occurred",Snackbar.LENGTH_LONG).show()
                }
            })
    }
}