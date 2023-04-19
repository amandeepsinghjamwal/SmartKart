package com.example.myapplication.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.myapplication.ApplicationClass
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
    private var _binding:FragmentProfileBinding ?= null
    private val binding get() =_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentProfileBinding.inflate(inflater,container,false)

        binding.myOrders.setOnClickListener{
            (activity as HomeScreen).gotoMyOrders()
        }
        binding.wishList.setOnClickListener{
            (activity as HomeScreen).gotoWishlist()
        }
        binding.changePassword.setOnClickListener {
            (activity as HomeScreen).gotoResetPassword()
        }
        binding.logOut.setOnClickListener{

            (activity as HomeScreen).showAlert("Log Out","Do you really want to log out of device?"){
                if(it){
                    ApplicationClass.editor!!.clear().apply()
                    (activity as HomeScreen).gotoLogin()
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if((activity as HomeScreen).checkPermission() && ApplicationClass.sharedPreferences!!.getBoolean("Notification",false)){
                binding.notificationsButton.isChecked = true
                ApplicationClass.editor!!.putBoolean("Notification", true).apply()
            }
            else if (!(activity as HomeScreen).checkPermission() or !ApplicationClass.sharedPreferences!!.getBoolean("Notification",false)) {
                binding.notificationsButton.isChecked = false
                ApplicationClass.editor!!.putBoolean("Notification", false).apply()
            }
        }

        binding.notificationsButton.isChecked =
            ApplicationClass.sharedPreferences!!.getBoolean("Notification",false)
        binding.notificationsButton.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                if((activity as HomeScreen).checkPermission() && ApplicationClass.sharedPreferences!!.getBoolean("Notification",false)){
                    ApplicationClass.editor!!.putBoolean("Notification",true).apply()
                    binding.notificationsButton.isChecked=true
                }
                else if(!(activity as HomeScreen).checkPermission()){
                    Toast.makeText(requireContext(),"You need to give notification permission from setting",Toast.LENGTH_SHORT).show()
                    binding.notificationsButton.isChecked=false
                    ApplicationClass.editor!!.putBoolean("Notification",false).apply()
                }
            }
            if(ApplicationClass.sharedPreferences!!.getBoolean("Notification",false)){
                ApplicationClass.editor!!.putBoolean("Notification",false).apply()
                binding.notificationsButton.isChecked=false
            }
            else{
                ApplicationClass.editor!!.putBoolean("Notification",true).apply()
                binding.notificationsButton.isChecked=true
            }
        }
        binding.profileDetails.setOnClickListener {
            binding.profileDetailsView.visibility=View.VISIBLE
        }
        binding.profileDetailsView.setOnClickListener{
            binding.profileDetailsView.visibility=View.GONE
        }
        val uid=ApplicationClass.sharedPreferences!!.getString("userId","email").toString()

        val name=ApplicationClass.sharedPreferences!!.getString("Name","User").toString()
        val email=ApplicationClass.sharedPreferences!!.getString("Email","User").toString()
        val mobileNumber=ApplicationClass.sharedPreferences!!.getString("MobileNo","User").toString()

        binding.userNameCard.text=name
        binding.userEmailCard.text=email
        binding.userNumberCard.text=mobileNumber
        binding.userName.text=getString(R.string.hi,name)
        binding.userEmail.text=email
        binding.raiseIssue.setOnClickListener {
            val i= Intent(Intent.ACTION_SENDTO).apply {
                val uriText = "mailto:" + Uri.encode("smartkart@example.com") +
                        "?subject=" + Uri.encode("issue: $uid")
                data=Uri.parse(uriText)
            }
                requireContext().startActivity(Intent.createChooser(i,"Choose"))
        }
        return  binding.root
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